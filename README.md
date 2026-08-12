# Clash Tools

Ferramentas utilitárias para Clash of Clans.

## Funcionalidades

- Exportação da Liga de Guerras para Excel (`GET /api/league/export`).
- Frontend React básico para acionar a geração da planilha.

## Como executar

### Backend

```bash
mvn spring-boot:run
```

O backend ficará disponível em `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend ficará disponível em `http://localhost:3000` e se comunica com o backend através do proxy configurado no `vite.config.ts`.

## Build do frontend

```bash
cd frontend
npm run build
```

Os arquivos estáticos serão gerados em `frontend/dist/`.
