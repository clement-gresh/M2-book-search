# README
# booksearchengine

### Membre du groupe:

Zimeng ZHANG.  Yajie LIU.  Clement GRESH

### Instructions pour l'exécution du projet:

**Base de donnée:**
Installer MongoDB sur votre ordinateur.
Utiliser la commande suivante pour lancer la base de données: 

```bash
*mongod --dbpath="chemin vers le répertoire data"*
```

Utiliser cette commande pour créer une base de données appelée TestDB: 

```bash
*use TestDB*
```

---

**Backend:**

Il existe plusieurs façons d'exécuter une application Spring Boot sur votre ordinateur local. Une manière consiste à exécuter la méthode main depuis l'IDE. Ouvrir le projet dans un IDE tel que Eclipse ou Intellij IDEA. Exécuter la méthode main dans la classe *‘com.searchgutenberg.booksearchengine.BooksearchengineApplication’* depuis l'IDE.

---

**Frontend:**

Une fois que le back-end a démarré, vous pouvez démarrer le front-end. Utilisez cd *frontend* ou n'importe quel IDE (VS Code, WebStorm...) pour ouvrir ce dossier.

Assurez-vous que le back-end est en cours d'exécution, puis vous pouvez utiliser:

```bash
*npm install*
```

```bash
*npm run dev*
```

pour exécuter la partie fronend. Ouvrez [http://127.0.0.1:5173/](http://127.0.0.1:5173/) avec votre navigateur pour voir le résultat.
