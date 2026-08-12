import { useEffect, useState } from 'react'

interface DayData {
  id: number
  warDay: number
  attackStars: number
  defenseStars: number
}

interface Player {
  id: number
  playerTag: string
  playerName: string
  totalAttackStars: number
  totalDefenseStars: number
  totalStars: number
  days: DayData[]
}

interface LeagueHistory {
  id: number
  clanTag: string
  clanName: string
  season: string
  filePath: string
  generatedAt: string
  players: Player[]
}

interface ExportResponse {
  message: string
  filePath: string
}

function App() {
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState<ExportResponse | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [histories, setHistories] = useState<LeagueHistory[]>([])
  const [selectedHistory, setSelectedHistory] = useState<LeagueHistory | null>(null)
  const [selectedPlayers, setSelectedPlayers] = useState<Player[]>([])
  const [loadingPlayers, setLoadingPlayers] = useState(false)
  const [tagFilter, setTagFilter] = useState('')
  const [seasonFilter, setSeasonFilter] = useState('')

  const handleExport = async () => {
    setLoading(true)
    setResult(null)
    setError(null)

    try {
      const response = await fetch('/api/league/export')
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${response.statusText}`)
      }
      const data: ExportResponse = await response.json()
      setResult(data)
      loadHistories()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro inesperado')
    } finally {
      setLoading(false)
    }
  }

  const loadHistories = async () => {
    try {
      const params = new URLSearchParams()
      if (tagFilter.trim()) params.set('tag', tagFilter.trim())
      if (seasonFilter.trim()) params.set('season', seasonFilter.trim())
      const url = params.toString()
        ? `/api/league/history/clan?${params.toString()}`
        : '/api/league/history'
      const response = await fetch(url)
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${response.statusText}`)
      }
      const data: LeagueHistory[] = await response.json()
      setHistories(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar histórico')
    }
  }

  const loadPlayers = async (history: LeagueHistory) => {
    setSelectedHistory(history)
    setSelectedPlayers([])
    setLoadingPlayers(true)
    try {
      const response = await fetch(`/api/league/history/${history.id}/players`)
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${response.statusText}`)
      }
      const data: Player[] = await response.json()
      setSelectedPlayers(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar jogadores')
    } finally {
      setLoadingPlayers(false)
    }
  }

  useEffect(() => {
    loadHistories()
  }, [])

  return (
    <div className="container">
      <h1>Clash Tools</h1>
      <p>Geração de planilha da Liga de Guerras</p>
      <button onClick={handleExport} disabled={loading}>
        {loading ? 'Gerando...' : 'Gerar Excel'}
      </button>

      {result && (
        <div className="message success">
          <strong>{result.message}</strong>
          <br />
          <small>{result.filePath}</small>
        </div>
      )}

      {error && <div className="message error">{error}</div>}

      <div className="filters">
        <h2>Histórico de Ligas</h2>
        <div className="filter-row">
          <input
            type="text"
            placeholder="Tag do clã (ex: #PVQ828J)"
            value={tagFilter}
            onChange={(e) => setTagFilter(e.target.value)}
          />
          <input
            type="text"
            placeholder="Season (ex: 2026-08)"
            value={seasonFilter}
            onChange={(e) => setSeasonFilter(e.target.value)}
          />
          <button className="secondary" onClick={loadHistories}>Buscar</button>
        </div>
      </div>

      {histories.length === 0 ? (
        <p className="empty">Nenhum histórico encontrado.</p>
      ) : (
        <ul className="history-list">
          {histories.map((history) => (
            <li key={history.id}>
              <button
                className="history-item"
                onClick={() => loadPlayers(history)}
                disabled={loadingPlayers}
              >
                <span className="clan-name">{history.clanName}</span>
                <span className="meta">
                  {history.season} &bull; {new Date(history.generatedAt).toLocaleString()} &bull; {history.players.length} jogadores
                </span>
              </button>
            </li>
          ))}
        </ul>
      )}

      {selectedHistory && (
        <div className="players-section">
          <h3>
            Jogadores - {selectedHistory.clanName} ({selectedHistory.season})
          </h3>
          {loadingPlayers ? (
            <p>Carregando jogadores...</p>
          ) : selectedPlayers.length === 0 ? (
            <p className="empty">Nenhum jogador encontrado.</p>
          ) : (
            <table className="players-table">
              <thead>
                <tr>
                  <th>Jogador</th>
                  <th>Tag</th>
                  <th>Estrelas Ataque</th>
                  <th>Estrelas Defesa</th>
                  <th>Total</th>
                  <th>Dias</th>
                </tr>
              </thead>
              <tbody>
                {selectedPlayers.map((player) => (
                  <tr key={player.id}>
                    <td>{player.playerName}</td>
                    <td>{player.playerTag}</td>
                    <td>{player.totalAttackStars}</td>
                    <td>{player.totalDefenseStars}</td>
                    <td>{player.totalStars}</td>
                    <td>
                      {player.days
                        .map((d) => `${d.attackStars}/${d.defenseStars}`)
                        .join(', ')}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  )
}

export default App
