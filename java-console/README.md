# 🍔 Brasil Burger - Application Console Java

Application console de gestion du catalogue Brasil Burger (Burgers, Compléments, Menus).

## 📋 Prérequis

- **Java** 17 ou supérieur
- **Maven** 3.6+
- **PostgreSQL** (Neon Cloud)
- **Compte Cloudinary** (stockage images)

## 🚀 Installation

### 1. Cloner le Projet

\`\`\`bash
git clone https://github.com/DIALLO667/BrasilBurger.git
cd BrasilBurger
git checkout java
cd java-console
\`\`\`

### 2. Configuration Base de Données

Créer `src/main/resources/database.properties` :

\`\`\`properties
db.url=jdbc:postgresql://VOTRE_HOST_NEON:5432/neondb?sslmode=require
db.username=VOTRE_USERNAME
db.password=VOTRE_PASSWORD
db.driver=org.postgresql.Driver
\`\`\`

### 3. Configuration Cloudinary

Créer `src/main/resources/cloudinary.properties` :

\`\`\`properties
cloudinary.cloud_name=VOTRE_CLOUD_NAME
cloudinary.api_key=VOTRE_API_KEY
cloudinary.api_secret=VOTRE_API_SECRET
cloudinary.folder=brasil-burger
\`\`\`

**Obtenir credentials** : https://cloudinary.com/console

## 🛠️ Compilation

\`\`\`bash
mvn clean compile
\`\`\`

## ▶️ Exécution

### Mode Maven

\`\`\`bash
mvn exec:java -Dexec.mainClass="sn.brasilburger.Main"
\`\`\`

### JAR Exécutable

\`\`\`bash
# Créer le JAR
mvn clean package

# Exécuter
java -jar target/java-console-1.0-SNAPSHOT-jar-with-dependencies.jar
\`\`\`

## 📂 Structure du Projet

\`\`\`
java-console/
├── src/main/java/sn/brasilburger/
│   ├── Main.java                    # Point d'entrée
│   ├── config/                      # Configuration (DB, Cloudinary)
│   ├── entity/                      # Modèles (Burger, Complement, Menu)
│   ├── repository/                  # Accès données (DAO)
│   ├── service/                     # Logique métier
│   └── view/                        # Interface console
├── src/main/resources/
│   ├── database.properties          # Config BD (non versionné)
│   └── cloudinary.properties        # Config Cloudinary (non versionné)
└── pom.xml                          # Configuration Maven
\`\`\`

## 🎯 Fonctionnalités

### Gestion des Burgers
- ✅ Ajouter un burger (avec upload image optionnel)
- ✅ Lister les burgers (actifs/tous)
- ✅ Modifier un burger (changement image)
- ✅ Archiver un burger (soft delete)
- ✅ Rechercher par fourchette de prix

### Gestion des Compléments
- ✅ Ajouter un complément (BOISSON ou FRITES)
- ✅ Lister par type
- ✅ Modifier un complément
- ✅ Archiver un complément

### Gestion des Menus
- ✅ Créer un menu (sélection burger + boisson + frites)
- ✅ Calcul automatique du prix total
- ✅ Lister les menus avec détails
- ✅ Modifier un menu (recalcul automatique du prix)
- ✅ Archiver un menu

## 🗄️ Base de Données

**PostgreSQL** hébergé sur **Neon Cloud** :
- Tables : Burger, Complement, Menu
- Images stockées sur Cloudinary (URLs en BD)

## 📦 Dépendances

- **PostgreSQL JDBC** 42.7.1
- **Cloudinary SDK** 1.36.0
- **JUnit 5** 5.10.1

## 👨‍💻 Auteur

**Nom** : Amadou DIALLO  
**Projet** : Brasil Burger - Gestion Catalogue  
**GitHub** : https://github.com/DIALLO667/BrasilBurger

## 📄 Licence

Projet académique - Formation Java
\`\`\`

**Sauvegarder** : `Cmd+S`
```bash
git add README.md
git commit -m "docs: README complet avec installation, configuration, execution, structure projet"
```
