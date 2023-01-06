## Technicians and Interventions Scheduling for Telecommunications
### Challenge ROADEF 2007 - France Telecom

Projet réalisé par Kea Horvath en 2022 dans le cadre de l'UE de Challenge Algorithmique (supervisé par Aurélien Froger).


### Classes exécutables:
#### Algorithm
L'exécutable Algorithm.java prend un dossier contenant un jeu de données en paramètre et affiche une solution dans le terminal.

On peut changer le tri réalisé par l'algorithme dans le fichier data/Intervention dans la fonction "compareTo" (ligne 124). Il faudra penser à recompiler le jar.

  ```sh
  java -jar jar/algorithm.jar <absolutePathToFolder>
  ```


#### Checker
L'exécutable Checker.java prend en paramètre un dossier contenant un jeu de données et une solution et indique dans le terminal si la solution est réalisable.
  ```sh
  java -jar jar/checker.jar <absolutePathToFolder>
  ```


#### Evaluator
L'exécutable Evaluator.java prend en paramètre un dossier contenant un jeu de données et une solution et affiche dans le terminal son coût.
  ```sh
  java -jar jar/evaluator.jar <absolutePathToFolder>
  ```


#### InstanceReader
L'exécutable InstanceReader.java prend un dossier contenant un jeu de données en paramètre et affiche les données dans le terminal.
  ```sh
  java -jar jar/instanceReader.jar <absolutePathToFolder>
  ```


#### SolutionReader
L'exécutable SolutionReader.java prend un dossier contenant la solution d'un jeu de données en paramètre et affiche ce résultat dans le terminal.
  ```sh
  java -jar jar/solutionReader.jar <absolutePathToFolder>
  ```


#### Solver
L'exécutable Solver.java prend en paramètres un dossier contenant un jeu de données et une durée limite (en secondes) et affiche une solution dans le terminal.
  ```sh
  java -jar jar/solver.jar <absolutePathToFolder> timeLimit
  ```


#### Results

L'exécutable Results.java prend en paramètre un dossier contenant 10 dossiers de jeux de données (dataSet) et affiche de façon succincte les solutions pour tous les jeux de données dans le terminal.

Pas de jar disponible. 


#### RandomSort

L'exécutable RandomSort.java prend en paramètre un dossier contenant 10 dossiers de jeux de données (dataSet) et un entier i et affiche de façon succincte les meilleures solutions trouvées au bout de i itérations pour tous les jeux de données dans le terminal.

Il faut s'assurer que la ligne 96 du fichier Algorithm.java soit décommentée (fonction getAndSortInterventions).

Pas de jar disponible.