ArenaMenu - /arenas opens a 9-slot chest with the 6 DuelCore arenas. Click one to teleport.
Runs: execute in duelcore:arenas run tp <you> <x> <150> <z>   (spawn 1 of each arena)

BUILD (needs JDK 25 + Gradle 9, or open the folder in IntelliJ and run the "jar" task):
    gradle jar
Result: build/libs/ArenaMenu.jar  -> put in your server's plugins/ folder, restart.

Needs op (permission arenamenu.use, default op).
Edit coordinates in src/main/java/dev/arenamenu/ArenaMenu.java (ARENAS list) if your slots change.
