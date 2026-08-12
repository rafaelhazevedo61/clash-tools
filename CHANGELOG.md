# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato é baseado no conceito de [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/).

## [Unreleased]

## [1.3.0] - 2026-08-12

### Added

- Frontend evoluído para exibir histórico de ligas e jogadores.
- Tela de filtros por tag do clã e season.
- Listagem de ligas salvas com data de geração e quantidade de jogadores.
- Tabela de jogadores com totais de ataque, defesa, estrelas gerais e dias de guerra.
- Navegação por abas (Gerar Excel / Histórico).
- Histórico agrupado por clã com expansão para visualizar registros de cada season.

## [1.2.0] - 2026-08-12

### Added

- Persistência dos dados da liga de guerras em banco H2.
- Entidades `LeagueHistoryEntity`, `PlayerHistoryEntity` e `PlayerDayDataEntity` com repositórios Spring Data JPA.
- `LeagueHistoryService` para salvar e consultar histórico de ligas e jogadores.
- Endpoints de histórico:
  - `GET /api/league/history` — lista todas as ligas salvas.
  - `GET /api/league/history/clan?tag={tag}&season={season}` — filtra por clã e season.
  - `GET /api/league/history/{id}/players` — lista jogadores de uma liga salva.
- Fluxo de exportação agora salva automaticamente os dados processados no banco ao gerar o Excel.
- Console H2 habilitado em `/h2-console` para consultas diretas durante o desenvolvimento.

## [1.1.0] - 2026-08-12

### Added

- Frontend React + Vite integrado ao repositório, com tela inicial para acionar a geração da planilha da liga de guerras.
- Paralelização do processamento dos clãs com `CompletableFuture`, reduzindo o tempo total de coleta dos dados das guerras.
- Configuração de proxy no Vite (`/api -> http://localhost:8080`) para facilitar o desenvolvimento local.
- `README.md` com instruções de execução do backend e do frontend.

### Fixed

- Removido o último `RuntimeException` do fluxo de exportação. Quando um clã não possui registros de guerra, a aplicação apenas registra um log informativo e continua processando os demais clãs.
- Configuração de encoding UTF-8 para logs (`logging.charset.console=UTF-8` no `application.yml` e `-Dfile.encoding=UTF-8` no plugin `spring-boot-maven-plugin`) para garantir acentos corretos no console.

## [1.0.0] - 2026-08-12

### Added

- Criação inicial do projeto `clash-tools`, isolando o serviço `exportLeagueFileV3` do projeto legado.
- Estrutura Spring Boot 3.4.4 com Java 17, Maven, Apache POI, Lombok, HttpClient nativo, Jackson e SLF4J.
- Endpoint `GET /api/league/export` para geração do arquivo Excel da liga mensal.
- Modelagem separada das entidades da API do Clash of Clans (`ClanWarLeagueGroup`, `ClanWarLeagueWarRegistry`, `ClanWarLeagueWarClan`, `ClanWarLeagueWarMembers`, etc.).
- Geração de planilha Excel com 7 colunas de dias de guerra, totais de ataque, defesa e geral.
- Configuração externa via `application.yml` para URLs da API, diretório de saída e prefixo do arquivo.
- Arquivo `clans.yml` importado no `application.yml` contendo todos os 16 clãs do projeto original.
- Carregamento do bearer token a partir de arquivo externo (`./secrets/token.txt`), simulando futuro volume mount no EKS.
- Teste unitário para cálculo de estrelas no `ClanWarLeagueExportService`.

### Changed

- Nome do projeto ajustado de `clash-cwl-exporter` para `clash-tools` para permitir futuras funcionalidades além da exportação do Excel.
- Response do endpoint alterado para retornar apenas uma mensagem de sucesso e o caminho do arquivo gerado, em vez do binário do Excel.
- Tratamento do erro 404 na busca da liga de guerras alterado: em vez de lançar `RuntimeException`, a aplicação apenas registra `CLASH-TOOLS-LOG:::::: REGISTROS PARA CLÃ NÃO ENCONTRADO` e continua.
- Tratamento de clãs sem registros de guerra alterado: em vez de lançar `RuntimeException`, a aplicação registra `CLASH-TOOLS-LOG:::::: NENHUM REGISTRO DE GUERRA ENCONTRADO` e segue sem interromper a execução.

### Fixed

- Corrigido erro de inicialização por `clash.clans` nulo ao importar corretamente o `clans.yml`.
- Corrigida a falta de alguns campos nos modelos da API de liga de guerras.
- Corrigido retorno do endpoint para evitar exibição de binário `.xlsx` como texto no Postman.
