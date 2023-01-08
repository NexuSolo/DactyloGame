
# DactyloGame

Projet de CPOO de 3ᵉ année de licence informatique a l'université de Paris Cité.
## Lancement

Pour lancer le projet utiliser la commande :

```bash
  ./gradlew run
```

Vous pouvez aussi créer un .jar et l'exécuter à l'aide de la commande :

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

Pour clean le projet :

```bash
  ./gradlew clean
  ```
## Fonctionnalité

- 2 modes d'entrainement
- Mode solo avec des niveaux de difficulté
- Mode multijoueurs
- Design responsive
- Options sauvegardées
- Graphique de statistique en fin de partie
## Choix technique

Cette application utilise javaFX, nous avons utilisé [scene Builder](https://gluonhq.com/products/scene-builder/) pour créer les interfaces graphiques.

Nous avons décidé pour le mode multijoueurs que le client ne servirait que d'interface et que toutes les actions seront traitées du couté serveur.

Des fichiers de configurations sont sauvegardés dans le dossier home/.Dactylo
## Tests

Si vous voulez lancer les tests il faut décommenter la ligne 48 de SoloModel.java et la ligne 45 de EntrainementModel.java
Pour lancer les tests, utilisez la commande suivante :

```bash
  ./gradlew test
```

## Auteurs

- Theau Nicolas 22011387[@theau](https://gaufre.informatique.univ-paris-diderot.fr/theau)
- René Tom 22007185 [@rene](https://gaufre.informatique.univ-paris-diderot.fr/rene)

