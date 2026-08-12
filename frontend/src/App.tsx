import { useEffect, useState } from 'react'

type Tab = 'export' | 'history'

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
}

interface ExportResponse {
  message: string
  filePath: string
}

interface ClearResponse {
  message: string
  deleted: number
}

interface ClanGroup {
  tag: string
  name: string
  histories: LeagueHistory[]
}

function App() {
  const [activeTab, setActiveTab] = useState<Tab>('export')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState<ExportResponse | null>(null)
  const [clearResult, setClearResult] = useState<ClearResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  const [histories, setHistories] = useState<LeagueHistory[]>([])
  const [expandedClan, setExpandedClan] = useState<string | null>(null)
  const [selectedHistory, setSelectedHistory] = useState<LeagueHistory | null>(null)
  const [selectedPlayers, setSelectedPlayers] = useState<Player[]>([])
  const [loadingPlayers, setLoadingPlayers] = useState(false)

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
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro inesperado')
    } finally {
      setLoading(false)
    }
  }

  const loadHistories = async () => {
    try {
      const response = await fetch('/api/league/history')
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${response.statusText}`)
      }
      const data: LeagueHistory[] = await response.json()
      setHistories(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar histórico')
    }
  }

  const handleClear = async () => {
    if (!confirm('Deseja realmente limpar todo o histórico?')) {
      return
    }
    setLoading(true)
    setClearResult(null)
    setError(null)
    try {
      const response = await fetch('/api/league/history', { method: 'DELETE' })
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${response.statusText}`)
      }
      const data: ClearResponse = await response.json()
      setClearResult(data)
      setHistories([])
      setSelectedHistory(null)
      setSelectedPlayers([])
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao limpar histórico')
    } finally {
      setLoading(false)
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
    if (activeTab === 'history') {
      loadHistories()
    }
  }, [activeTab])

  const clanGroups: ClanGroup[] = histories.reduce((groups, history) => {
    const existing = groups.find((g) => g.tag === history.clanTag)
    if (existing) {
      existing.histories.push(history)
      return groups
    }
    groups.push({
      tag: history.clanTag,
      name: history.clanName || history.clanTag,
      histories: [history],
    })
    return groups
  }, [] as ClanGroup[])

  return (
    <div className="container">
      <h1>Clash Tools</h1>
      <div className="tabs">
        <button
          className={activeTab === 'export' ? 'active' : ''}
          onClick={() => setActiveTab('export')}
        >
          Gerar Excel
        </button>
        <button
          className={activeTab === 'history' ? 'active' : ''}
          onClick={() => setActiveTab('history')}
        >
          Histórico
        </button>
      </div>

      {error && <div className="message error">{error}</div>}
      {clearResult && (
        <div className="message success">
          {clearResult.message} ({clearResult.deleted} registro(s) removido(s))
        </div>
      )}

      {activeTab === 'export' && (
        <div className="tab-content">
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
        </div>
      )}

      {activeTab === 'history' && (
        <div className="tab-content">
          <div className="history-header">
            <button className="danger" onClick={handleClear} disabled={loading}>
              Limpar histórico
            </button>
          </div>

          {histories.length === 0 ? (
            <p className="empty">Nenhum histórico encontrado.</p>
          ) : (
            <div className="history-layout">
              <div className="clan-list">
                {clanGroups.map((clan) => (
                  <div key={clan.tag} className="clan-group">
                    <button
                      className="clan-button"
                      onClick={() =>
                        setExpandedClan(expandedClan === clan.tag ? null : clan.tag)
                      }
                    >
                      <span className="clan-name">{clan.name}</span>
                      <span className="meta">
                        {clan.tag} &bull; {clan.histories.length} registro(s)
                      </span>
                    </button>

                    {expandedClan === clan.tag && (
                      <ul className="history-list">
                        {clan.histories.map((history) => (
                          <li key={history.id}>
                            <button
                              className={`history-item ${selectedHistory?.id === history.id ? 'selected' : ''}`}
                              onClick={() => loadPlayers(history)}
                            >
                              <span>{history.season || 'Sem season'}</span>
                              <span className="meta">
                                {new Date(history.generatedAt).toLocaleString()}
                              </span>
                            </button>
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                ))}
              </div>

              {selectedHistory && (
                <div className="players-section">
                  <h3>
                    {selectedHistory.clanName || selectedHistory.clanTag} &bull; {selectedHistory.season || 'Sem season'}
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
                          <th>Ataque</th>
                          <th>Defesa</th>
                          <th>Total</th>
                          <th>Dias</th>
                        </tr>
                      </thead>
                      <tbody>
                        {selectedPlayers.map((player) => (
                          <tr key={player.id}>
                            <td>{player.playerName || '-'}</td>
                            <td>{player.playerTag || '-'}</td>
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
          )}
        </div>
      )}
    </div>
  )
}

export default App
