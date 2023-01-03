
# DactyloGame

Projet de CPOO de 3ème année de licence informatique a l'université de Paris Cité.
## Lancement

Pour lancer le projet utiliser la commande :

```bash
  ./gradlew run
```

Vous pouvez aussi créer un .jar et l'executer a l'aide de la commande :

```bash
  ./gradlew shadowJar
  java -jar .\app\build\libs\DactyloGame.jar
```

Pour lancer un serveur local vous pouvez utiliser la commande :

```bash
  ./gradlew Serveur
```

Vous pouvez aussi définir un port au lancement de votre serveur :

```bash
  ./gradlew serveur -Pport=xxxx
```
## Features

- 2 modes d'entrainement
- Mode solo avec des niveaux de difficulté
- Mode multijoueurs
- Design responsives
- Options sauvegardé
- Graphique de statistique en fin de partie
## Choix technique

Cette application utilise javaFX, nous avons utilisé [scene Builder](https://gluonhq.com/products/scene-builder/) pour créer les interfaces graphique.

Nous avons décidé pour le mode multijouers que le client ne servirai que d'interface et que toutes les actions seront traité du couté serveur.

Des fichiers de configurations sont sauvegardé dans le dossier home/.Dactylo
## Running Tests

Pour lancer les tests utilisez la commande suivante :

```bash
  ./gradlew test
```

## Auteurs

- Theau Nicolas 22011387[@theau](https://gaufre.informatique.univ-paris-diderot.fr/theau)
- René Tom xxxxxxx [@rene](https://gaufre.informatique.univ-paris-diderot.fr/rene)

