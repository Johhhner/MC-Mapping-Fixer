## **COWS HAVE NOT YET BEEN ADDED**

  Please make sure to create an issue if ya' notice any cows sneakin' in to this project. I will do my best to try'n' capture 'em fellas and move 'em somewhere else so they ain't eatin' all the code; this code ain't healthy for 'em!

## **WHAT DOES THIS DO?**
This project aims to remap all of the lambdas in the output.tsrg mapping file for the forge create mappings task to their propper names in the compiled forge project, that way the project can be used to debug any mods without worrying about mixin's on lambdas.
This project may also work with any mod launcher provided there is a tsrg mapping file, however.
  
## **BUILDING THE PROJECT**

  All ya' gotta' do is clone using "git clone" and whatever the rest of that command was (I ain't remember), and then run the "gradle \[IDE\]" task to generate the files for ya' environment. Then ya' jus' gotta' run "gradlew assemble" and ya' done. Easy as 1, 2, 3!\
  And by the way, NO: THERE IS NO "COW" TASK! So ya' stuff should go 'n' look like this:
```
git clone [BLAH BLAH BLAH] (Again, I don't remember.)
gradlew [IDE]
gradlew assemble
gradlew COW (DO NOT DO THIS!!!)
```
## **RUNNING THE FILE**
  The jar file can easily be run like this:
```
[PATH TO MAPPING FIXER JAR] {PATH TO RECOMPILED MINECRAFT JAR} {PATH TO TSRG MAPPINGS FILE} {PATH TO FORGE BIN CLASSES}
```
Then ya' either gotta' wait like 5 seconds, or pray that you only have to wait 5 seconds.
