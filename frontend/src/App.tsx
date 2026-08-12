import { useState } from 'react'

interface ExportResponse {
  message: string
  filePath: string
}

function App() {
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState<ExportResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

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

  return (
    <div className="container">
      <h1>Clash Tools</h1>
      <p>Geração de planilha da Liga de Guerras</p>
      <button onClick={handleExport} disabled={loading}>
        {loading ? 'Gerando...' : 'Gerar Excel'}
      </button>

      {result && (
        <div className="message">
          <strong>{result.message}</strong>
          <br />
          <small>{result.filePath}</small>
        </div>
      )}

      {error && <div className={`message error`}>{error}</div>}
    </div>
  )
}

export default App
