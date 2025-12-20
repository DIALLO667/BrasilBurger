FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copier .csproj et restaurer
COPY *.csproj .
RUN dotnet restore

# Copier tout le reste
COPY . .

# Publier en Release
RUN dotnet publish -c Release -o /app/out

# Image finale
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY --from=build /app/out .

# Port 8080 pour Render
EXPOSE 8080
ENV PORT=8080
ENV ASPNETCORE_URLS=http://+:8080

ENTRYPOINT ["dotnet", "BrasilBurger.Client.dll"]