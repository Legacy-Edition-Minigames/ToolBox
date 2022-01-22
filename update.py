import os
import requests
import webbrowser
import shutil
from time import sleep
from zipfile import ZipFile

################
###   Load   ###
################
cfg_branch = "main"

W  = '\033[0m'  # white
R  = '\033[31m' # red
G  = '\033[32m' # green
O  = '\033[33m' # orange
B  = '\033[36m' # blue
P  = '\033[35m' # purple
E  = '\033[30;1m'


def cls():
    os.system('cls' 
    if os.name=='nt'
    else 'clear')
    
try:
    f = open("updater.cfg")
    cfg_branch = f.read()
except IOError:
    f = open("updater.cfg", "w")
    f.write("main")
    f.close()
finally:
    f.close()


################
###   GUIs   ###
################

def mainMenu():
    cls()
    print("=======================================================")
    print(G+"Legacy Edition Battle (LEB) Updater"+W)
    print("=======================================================")
    print("")
    print(P+"Choose an action below:"+W)
    print("")
    print("1. Update to the latest commit available")
    print("2. Perform a Clean update to the latest commit available")
    print("3. Change branch (current selected branch: ", B+cfg_branch+W, ")")
    print("4. Open GitHub project page")
    print("")
    print("5. Exit")
    print("")
    action = input(B+"Input: "+W)
    
    if action == "1":
        updater()
    elif action == "2":
        cleanUpdater()
    elif action == "3":
        changeBranch()
    elif action == "4":
        webbrowser.open('http://example.com')
    elif action == "5":
        exit()
    else:
        mainMenu()

def updater():
    cls()
    print("=======================================================")
    print(G+"Update to the latest commit available"+W)
    print("=======================================================")
    print("")
    print("This will update you current server to the latest version (commit) uploaded to the GitHub repository.")
    print("")
    print(P+"Are you sure you want to update to the last commit?"+W)
    print("")
    action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
    if action.lower() == "y":
        print("User authorised operation, executing...")
         #continue execution
    elif action.lower() == "n":
        mainMenu()
    else:
        updater()
    print("")
    backup()
    downloadInstall()
    restore()
    print()
    print(G+"*** Update successful! ***"+W)
    print("")
    action2 = input(B+"Press ENTER to continue . . ."+W)
    mainMenu()

def cleanUpdater():
    cls()
    print("=======================================================")
    print(G+"Perform a Clean update to the latest commit available"+W)
    print("=======================================================")
    print("")
    print(R+"WARNING!: Performing a Clean Update will erase all player data save data (ex: archivements).")
    print("It's recommended to backup playerdata to avoid loosing player-specific-settings, custom presets, archivements,...")
    print("If you are troubleshooting problems, feel free to continue." +W)
    print("")
    print(P+"Are you sure you want to ERASE everything and install again?"+W)
    print("")
    action = input(B+"Input [Y/N]: "+W)
    if action.lower() == "y":
        print(E+"User authorised operation, executing..."+W)
         #continue execution
    elif action.lower() == "n":
        mainMenu()
    else:
        cleanUpdater()
    print("")
    downloadInstall()
    print()
    print(G+"*** Clean Update successful! ***"+W)
    print("")
    action2 = input(B+"Press ENTER to continue . . ."+W)
    mainMenu()

    
def changeBranch():
    cls()
    print("=======================================================")
    print(B+"Change Branch"+W)
    print("=======================================================")
    print("")
    print("You can choose whatever branch you feel like using by selecting one of the displayed branches below.")
    print("The default (most stable and updated) branch is MAIN.")
    print("Using experimental or outdated branches might break the savedata of the server. Test with caution!")
    print("")
    print(G+"Default branches:"+W)
    print("1. main (default)")
    print("")
    print(P+"Avaible branches:"+W)
    print("2. testing")
    print("3. weed (?)")
    print("4. old-resetter")
    print("")
    print(R+"Old/Outdated branches:"+W)
    print("5. 1.17")
    print("6. 1.16.5")
    print("7. vanilla")
    print("")
    action = input(B+"Input: "+W)

    global cfg_branch
    
    if action == "1":
         cfg_branch = "main"
    elif action == "2":
        cfg_branch = "testing"
    elif action == "3":
        cfg_branch = "weed"
    elif action == "4":
        cfg_branch = "old-resetter"
    elif action == "5":
        cfg_branch = "1.17"
    elif action == "6":
        cfg_branch = "1.16.5"
    elif action == "7":
        cfg_branch = "vanilla"
    else:
        changeBranch()
    f = open("updater.cfg", "w")
    f.write(cfg_branch)
    f.close()
    mainMenu()


####################
###   Functions  ###
####################

def backup():
    print("Backing up...", end='')
    sleep(0.05)
    try:
        shutil.rmtree("leb_update_cache")
        os.mkdir("leb_update_cache")
    except OSError as error:
        os.mkdir("leb_update_cache")
        print("", end='')
        
    try:
        shutil.copytree('world/advancements', 'leb_update_cache/world/advancements')
    except OSError as error:
        print(error, end='')
    finally:
        print(G+"DONE"+W)


def downloadInstall():
    print(E+"Note: Due to GitHub limitations, download ETA is not available."+W)
    print("Downloading build" + E+ " (this can take up to 6 minutes)" + W + "...", end='')
    leb_zip = requests.get('https://github.com/DBTDerpbox/Legacy-Edition-Battle/archive/refs/heads/' + cfg_branch+ '.zip', allow_redirects=True)
    open("leb_update_cache/leb.zip", "wb").write(leb_zip.content)
    print(G+"DONE"+W)
    print("Removing old files...", end='')
    sleep(0.05)
    try:
        shutil.rmtree("world")
        shutil.rmtree("images")
        shutil.rmtree("config")
        os.remove(".gitignore")
        os.remove("INSTALLATION.md")
        os.remove("INSTALLATION-MINEHUT.md")
        os.remove("LICENSE")
        os.remove("README.md")
        os.remove("SCREENSHOTS.md")
    except Exception:
        pass
    print(G+"DONE"+W)
    print("Extracting files...", end='')
    sleep(0.05)
    with ZipFile('leb_update_cache/leb.zip', 'r') as zipObj:
        zipObj.extractall()
    print(G+"DONE"+W)
    print("Moving files...", end='')
    sleep(0.05)
    try:
        for filename in os.listdir('Legacy-Edition-Battle-' + cfg_branch):
            shutil.move('Legacy-Edition-Battle-' + cfg_branch + "/" + filename, filename)
        shutil.rmtree('Legacy-Edition-Battle-' + cfg_branch)
    except Exception:
        pass
    print(G+"DONE"+W)
    

def restore():
    print("Restoring backup...", end='')
    sleep(0.05)
    try:
        shutil.rmtree('world/advancements')
    except OSError as error:
        print("", end='')
    
    try:
        shutil.copytree('leb_update_cache/world/advancements', 'world/advancements')
    except OSError as error:
        print(R+error, end='')
        
    try:
        shutil.rmtree("leb_update_cache")
    except OSError as error:
        print(R+error, end='')
        
    print(G+"DONE"+W)




mainMenu()


### Tool created by PiporGames, with love, for LEB ###
