import os
import requests
import webbrowser
import shutil
from time import sleep
from zipfile import ZipFile
import os.path
import platform
from sys import exit


def stringTF(start,end,s):
    return s[s.find(start)+len(start):s.rfind(end)]
    

def cls():
    os.system('cls' 
    if os.name=='nt'
    else 'clear')

################
###   Load   ###
################

W  = '\033[0m'  # white
R  = '\033[31m' # red
G  = '\033[32m' # green
O  = '\033[33m' # orange
B  = '\033[36m' # blue
P  = '\033[35m' # purple
E  = '\033[30;1m' #gray


####### PROGRAM VERSION #######
cnt_program = 1.0
ver_program = G+"v"+str(cnt_program)+W

ver_info = O+"LEB-ToolBox "+ver_program+" changelog:\n"+W+"-"+G+" NEW "+W+"configuration file upgrader; no more settings lost when updating the program.\n"+W+"-"+G+" NEW"+W+" Legacy Resetter option is now avilable. Read the documentation or make a new server install to know more.\n"+W+"-"+G+" NEW "+W+"Dependecies required.\n"+W+"-"+G+" NEW "+W+"Now every update displays the changelog when launching for the first time, or before updating to a new version.\n"+W+"-"+G+" NEW "+W+"Welcome message.\n"+W+"-"+G+" NEW "+W+"See Changelog option in Main Menu.\n"+W
####### PROGRAM VERSION #######

cfg_branch = "main"
current_hash = R+"unknown"
check_for_updates = -1

online_count_program = 0
fabric = 0
dependencies = 0
optimize = 0
upnp = 0
viafabric = 0
minimotd = 0
server_scripts = 0
ram = 0
motd_sync = -1
legacy_resetter = -1
eula = 0
lebDebugDisableDownloadContent = 0
lebDebugKeepCache = 0

def readConfig():
    global cfg_branch
    global current_hash
    global motd_sync
    global check_for_updates
    global legacy_resetter
    isUpdating = 0
    try:      
        if os.path.isfile('LEB-ToolBox_old.exe') or os.path.isfile('LEB-ToolBox_old') or os.path.isfile('LEB-ToolBox_DELETE_ME.exe') or os.path.isfile('LEB-ToolBox_DELETE_ME'):
            isUpdating = 1
        if os.path.isfile('updater.cfg'):
            raw = open("updater.cfg", "r")
            f = raw.read()
            file_split = f.split("#/#")
            cfg_branch = file_split[0]
            motd_sync = int(file_split[1])
            current_hash = file_split[2]
            check_for_updates = int(file_split[3])
            legacy_resetter = int(file_split[4])
        else:
            print(O+"*******************************************************"+W)
            print(G+"Welcome to LEB-ToolBox "+ver_program+"!"+W)
            print(O+"*******************************************************"+W)
            print("Thanks for choosing LEB-ToolBox!")
            print("")
            print("LEB-ToolBox is a tool designed to make it easy for users to install, update and customize their own LEB installation.")
            print("To navigate through the program, wait until a blue <Input:> message appears and then use the numbers displayed onscreen to select your choice and press ENTER.")
            print("")
            print("If you encounter any problem, try doing a clean reinstall of everything or contact us on out Discord.")
            print(B+"LEB"+W+"-"+G+"ToolBox "+W+"created by Pi"+R+"por"+O+"Games"+W)
            print(E+"Legacy Edition"+O+" Battle"+W+" created by "+R+"DBTDerpbox "+E+"+"+B+" contributors"+W)
            print("Consider donating at"+O+" Patreon"+W+"! "+O+"patreon.com/DBTDerpbox"+W)
            print("")
            print("Have fun!")
            print("")
            action = input(B+"Press ENTER to continue . . .")
            raw = open("updater.cfg", "w")
            raw.write("main#/#1#/#unknown#/#1#/#0")
            raw.close()
    except:
        if isUpdating == 0:
            print(R+"An error has ocurred while trying to load the configuration file.")
            print(W+"If you have updated the program recently, it might mean that the program requires a configuration upgrade.")
            print("The program will try to convert the old configuration version to the new format required.")
            print("")
            print(P+"Do you want to continue?")
            action = input(B+"Input [Y/N] (defualt=Yes): "+W)
        else:
            action = "y"
        if action.lower() == "y":
            try:
                print("")
                print("Trying to convert old configuration...")
                raw = open("updater.cfg", "r")
                f = raw.read()
                file_split = f.split("#/#")
                cfg_branch = file_split[0]
                motd_sync = int(file_split[1])
                current_hash = file_split[2]
                check_for_updates = int(file_split[3])
            except:
                pass
            finally:
                if cfg_branch == "":
                    cfg_branch = "main"
                if motd_sync == -1:
                    motd_sync = 1
                if current_hash == "":
                    current_hash = R+"unknown"
                if check_for_updates == -1:
                    check_for_updates = 1
                raw.close()
                raw = open("updater.cfg", "w")
                print(str(motd_sync))
                raw.write(cfg_branch+"#/#"+str(motd_sync)+"#/#"+current_hash+"#/#"+str(check_for_updates)+"#/#0")
                raw.close()
                print("Conversion finished.")
                readConfig()
        elif action.lower() == "n":
            print("")
        else:
            raw = open("updater.cfg", "w")
            raw.write("main#/#1#/#unknown#/#1#/#0")
            raw.close()
    finally:
        raw.close()
if os.name=='nt':
    os.system('title LEB-ToolBox v'+str(cnt_program))

readConfig()
################
###   GUIs   ###
################

def mainMenu():
    readConfig()
    cls()
    print("=======================================================")
    print(G+"Legacy Edition Battle (LEB) ToolBox " + ver_program +W)
    print("=======================================================")
    print("")
    print(P+"Choose an action below:"+W)
    print("")
    try:
        f = open("fabric-server-launch.jar")
        print("0. Start LEB Server")
        print("1. Update LEB " + E +"(current commit instaled: " +G+current_hash+E+")"+W)
        f.close()
    except IOError:
        print("1. Install LEB")     
    print("2. Settings")
    print("3. Change branch (current selected branch: ", B+cfg_branch+W, ")")
    print("")
    print("4. Open GitHub project page")
    print("5. See the Changelog")
    print("")
    print("6. Exit")
    print("")
    action = input(B+"Input: "+W)
    if action == "0":
        try:
            f = open("fabric-server-launch.jar")
            f.close()
            cls()
            print(G+"****************************")
            print("*** LEB Server Launching ***")
            print("****************************"+W)
            print("")
            if platform.system() == "Linux":
                os.system("./Run-Linux.sh")
            elif platform.system() == "Darwin":
                os.system("Run-MacOS.sh")
            elif platform.system() == "Windows":
                os.system("Run-Windows.cmd")
            print("")
            print(R+"***************************")
            print("*** LEB Server Stopping ***")
            print("***************************"+W)
            print("")
            action = input(B+"Press ENTER to return to the main menu . . ."+W)
            mainMenu()
        except IOError:
            mainMenu()  
    elif action == "1":
        try:
            f = open("fabric-server-launch.jar")
            f.close()
            updateMenu()
        except IOError:
            installMenu()  
    elif action == "2":
        settingsMenu()
    elif action == "3":
        changeBranch()
    elif action == "4":
        webbrowser.open('https://github.com/DBTDerpbox/Legacy-Edition-Battle')
        mainMenu()
    elif action == "5":
        changeLog()
    elif action == "6":
        exit()
    elif action == "debug download":
        global lebDebugDisableDownloadContent
        lebDebugDisableDownloadContent = 1
        action = input(B+"forced LEB content download at install disabled untill next restart"+W)
        mainMenu()
    elif action == "debug cache":
        global lebDebugKeepCache
        lebDebugKeepCache = 1
        action = input(B+"forced LEB keep cache folder after installing"+W)
        mainMenu()
    elif action == "debug reinstall_s":
        reinstall()
    elif action == "debug install":
        installMenu()
    elif action == "debug setmotd":
        setMOTD()
    elif action == "debug cfu":
        var111 = checkForUpdates()
        print(str(var111))
        input("")
        mainMenu()
    else:
        mainMenu()

def installMenu():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print("=======================================================")
    print("")
    print(G+"Welcome to the LEB setup wizard!"+W)
    print("Thank you for downloading LEB. This wizard will help you setup your own LEB server instance.")
    print("The setup will ask you some questions before proceeding to install everything.")
    print("")
    print("Consider donating if you want to support this project. More info at the README file.")
    print("We hope you have fun!")
    print("")
    print(P+"Press " + B+ "ENTER " + P+ "to start the setup  . . ." + W)
    print("")
    action = input("")
    installMenu_2()
    
def installMenu_2():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Install type (1/1)"+W)
    print("=======================================================")
    print("")
    print(G+"Install type:"+W)
    print("1. Full Install"+W+":"+E+" This option will install every dependency and enchancement needed for LEB to work as intended."+W)
    print("2. Minimal Install"+W+":"+E+" This option will install only necesary dependencies for LEB to work, without any enchancements."+W)
    print("3. Custom Install"+W+":"+E+" You will be asked to allow the install of each component individually."+W)
    print("")
    print(P+"Choose a install type from above:"+W)
    print("")
    action = input(B+"Input: "+W)

    global fabric
    global dependencies
    global optimize
    global upnp
    global viafabric
    global server_scripts
    global minimotd
    global motd_sync
    global EULA
    eula = 0
    # set 0 / nothing = user choice
    # set 1 = install
    # set 2 = do not install
    if action == "1":
        fabric = 1
        dependencies = 1
        optimize = 1
        upnp = 1
        viafabric = 1
        minimotd = 1
        server_scripts = 1
        motd_sync = 1
        installMenu_3()
    elif action == "2":
        fabric = 1
        dependencies = 1
        optimize = 2
        upnp = 2
        viafabric = 2
        minimotd = 2
        server_scripts = 2
        motd_sync = 2
        installMenu_3()
    elif action == "3":
        fabric = 0
        dependencies = 0
        optimize = 0
        upnp = 0
        viafabric = 0
        minimotd = 0
        server_scripts = 0
        motd_sync = 0
        installMenu_3()
    else:
        installMenu_2()

        
def installMenu_3():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Components (1/2)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to install "+G+"Fabric"+B+"?"+W)
    print("Fabric is the core server component of the server and it " +R+"MUST"+W+" be installed not just make LEB work, but to actually have a server.")
    print("Besides upgrading the Fabric version for extraordinary circumstances, you should always install this component.")
    print("")
    print(R+"WARNING: LEB WON'T WORK IF THIS COMPONENT IS NOT INSTALLED!")
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global fabric   
    if fabric == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
        if action.lower() == "y":
            fabric = 1
            installMenu_4()
        elif action.lower() == "n":
            fabric = 2
            installMenu_4()
        else:
            installMenu_3()
    else:
        installMenu_4()


def installMenu_4():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Components (2/2)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to install "+G+"Dependencies (multiple components)"+B+"?"+W)
    print("LEB ships with a preset of required dependencies for it to work as intended. This dependencies " +R+"MUST"+W+" be installed to make LEB work as intended.")
    print(B+"The list of dependencies contains:")
    print(G+"- Fabric API")
    print("- Switchable Resource Packs")
    print("- Extended Structures")
    print("- Take Everything")
    print("- SnowballKB")
    print("- Starlight"+W)
    print("Besides upgrading the dependencies version for extraordinary circumstances, you should always install this component.")
    print("")
    print(R+"WARNING: LEB WON'T WORK IF THIS COMPONENT IS NOT INSTALLED!")
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global dependencies
    if dependencies == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W) 
        if action.lower() == "y":
            dependencies = 1
            installMenu_4_B()
        elif action.lower() == "n":
            dependencies = 2
            installMenu_4_B()
        else:
            installMenu_4()
    else:
        installMenu_4_B()

def installMenu_4_B():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (1/8)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to install "+G+"Optimization mods"+B+"?"+W)
    print("LEB Lobby loading delay and memory usage can be reduced with the help of some lightweight modifications to the server-side code.")
    print("Using this will optimize the server to use less of your system's resources without any drawbacks.")
    print(B+"The list of optimization mods contains:")
    print(G+"- FerriteCore")
    print("- Hydrogen")
    print("- Lithium"+W)
    print("")
    print(E+"This is an optional enchancement."+W)
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global optimize
    if optimize == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
        if action.lower() == "y":
            optimize = 1
            installMenu_5()
        elif action.lower() == "n":
            optimize = 2
            installMenu_5()
        else:
            installMenu_4_B()
    else:
        installMenu_5()

def installMenu_5():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (2/8)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to use "+G+"UPnP"+B+"?"+W)
    print("The Universal PlugAndPlay (UPnP) is a custom port forwarding protocol used to automatically open your router's port to the outside Internet.")
    print("This is a neat feature to have if you have problems with Port Forwarding or you don't know much about it, and your friends want to connect to your computer"+E+" (asuming you are not playing LAN)"+W+".")
    print("This enchancement doesn't work with all types and models of routers out there, check if yours have UPnP before installing!.")
    print("")
    print(E+"This is an optional enchancement."+W)
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global upnp
    if upnp == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
        if action.lower() == "y":
            upnp = 1
            installMenu_6()
        elif action.lower() == "n":
            upnp = 2
            installMenu_6()
        else:
            installMenu_5()
    else:
        installMenu_6()

        
def installMenu_6():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (3/8)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to use "+G+"ViaFabric"+B+"?"+W)
    print("ViaFabric is a mod that provides cross-version compatibility, allowing 1.18 client users to join the server.")
    print("This is a neat feature to have if you use mods, resourcepacks, or modified clients that require especifically version 1.18.")
    print("")
    print(E+"This is an optional enchancement."+W)
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global viafabric
    if viafabric == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W) 
        if action.lower() == "y":
            viafabric = 1
            installMenu_6_2()
        elif action.lower() == "n":
            viafabric = 2
            installMenu_6_2()
        else:
            installMenu_6()
    else:
        installMenu_6_2()

def installMenu_6_2():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (4/8)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to use "+G+"MiniMOTD"+B+"?"+W)
    print("MiniMOTD is a mod that provides fancy looking server status messages (MOTDs), featuring cool looking gradients, amoung other things.")
    print("This is a neat feature to have if you want to have cool looking server stats.")
    print("")
    print(E+"This is an optional enchancement."+W)
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global minimotd
    if minimotd == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W) 
        if action.lower() == "y":
            minimotd = 1
            installMenu_7()
        elif action.lower() == "n":
            minimotd = 2
            installMenu_7()
        else:
            installMenu_6_2()
    else:
        installMenu_7()


def installMenu_7():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (5/8)"+W)
    print("=======================================================")
    print("")
    print(B+"How much "+G+"RAM"+B+" do you want to allocate to LEB?"+W)
    print("Modified servers, such as LEB, require extra memory than default Minecraft servers.")
    print("You can set whatever amount of RAM (in GB) you want to use.")
    print("")
    print(E+"It's recommended to use"+G+" at least 3GB of RAM"+E+" to ensure LEB will work as intended."+W)
    print("")
    print(P+"How much RAM do you want to allocate (in GB)?:"+W)
    print("")
    global ram
    while True:
        try:
            ram = int(input(B+"Input " + G + "[in GB]" + B + ": "+W))
            break;
        except ValueError:
            installMenu_7()
    installMenu_7_B()

def installMenu_7_B():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (6/8)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to use "+G+"Legacy Resetter"+B+"?"+W)
    print("When enabled, LEB will revert to the old legacy map resetter system. ")
    print("This is somewhat more unstable and buggier, but require less RAM overall.")
    print("")
    print(R+"This is an alternative option. On normal circumstances, you shouldn't use this option."+W)
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global legacy_resetter
    if legacy_resetter == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
        if action.lower() == "y":
            legacy_resetter = 1
            installMenu_8()
        elif action.lower() == "n":
            legacy_resetter = 2
            installMenu_8()
        else:
            installMenu_7_B()
    else:
        installMenu_8()

def installMenu_8():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (7/8)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to use "+G+"MOTD Sync"+B+"?"+W)
    print("With MOTD Sync enabled, the server's MOTD Message will be synced with the latest official LEB commit's MOTD.")
    print("This is helpfull if you want to quickly determinate if you are running an old version of LEB.")
    print("")
    print(E+"This is an optional enchancement. You can change this setting at the LEB-Toolbox Settings Page."+W)
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global motd_sync
    global cfg_branch
    if motd_sync == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
        if action.lower() == "y":
            try:
                f = open("updater.cfg", "w")
                f.write(cfg_branch+"#/#1")
            finally:
                f.close()
            motd_sync = 1
            installMenu_9()
        elif action.lower() == "n":
            motd_sync = 2
            installMenu_9()
        else:
            installMenu_8()
    else:
        installMenu_9()

def installMenu_9():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Enchancements (8/8)"+W)
    print("=======================================================")
    print("")
    print(B+"Do you want to use "+G+"Server GUI"+B+"?"+W)
    print("With Server GUI enabled, upon server start, a detailed window will apear containg a memory graph, player list and command console.")
    print("This is a nice way of managing the LEB server, but it consumes some (not that much) resources.")
    print("Disabling this component will output instead a black terminal window with lots of debugging logs.")
    print("")
    print(E+"This is an optional enchancement."+W)
    print("")
    print(P+"Do you want to install this component?:"+W)
    print("")
    global server_scripts
    if server_scripts == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)  
        if action.lower() == "y":
            server_scripts = 1
            installMenu_10()
        elif action.lower() == "n":
            server_scripts = 2
            installMenu_10()
        else:
           installMenu_9()
    else:
        installMenu_10()


def installMenu_10():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"EULA Agreement"+W)
    print("=======================================================")
    print("")
    print(B+"Do you accept the "+G+"Minecraft's EULA"+B+"?"+W)
    print("For your server to run you must accept Minecraft's EULA.")
    print("The Minecraft's EULA contains information and rules about what you can do and can't do while using the game.")
    print("Agreement of the Minecraft's EULA is "+R+"strictly needed"+W+", otherwise your server would be illegal to operate and thus, won't open.")
    print("")
    print(R+"WARNING: LEB WON'T WORK IF MINECRAFT'S EULA IS NOT AGREED!")
    print("")
    print(P+"Do you want to accept the "+G+"Minecraft's EULA"+B+"?"+W)
    print("")
    global eula
    if eula == 0:
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)  
        if action.lower() == "y":
            eula = 1
            installMenu_11()
        elif action.lower() == "n":
            eula = 2
            installMenu_11()
        else:
           installMenu_10()
    else:
        installMenu_11()

        
def installMenu_11():
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Ready to Install"+W)
    print("=======================================================")
    print("")
    print(G+"Ready to install LEB!"+W)
    print("You are now ready to install LEB.")
    print("This program will now connect to the internet to download the required files.")
    print("")
    print(P+"Press " + B+ "ENTER " + P+ "to start installing . . ." + W)
    print("")
    action = input("")
    installMenu_12()

def installMenu_12():
    global fabric
    global dependencies
    global optimize
    global upnp
    global viafabric
    global minimotd
    global server_scripts
    global motd_sync
    global ram
    cls()
    print("=======================================================")
    print(G+"Install LEB"+W)
    print(G+"Installing..."+W)
    print("=======================================================")
    print("")
    print(G+"Installing LEB!"+W)
    print("")
    prepare()
    #fabric
    if fabric == 1:
        print("Downloading Fabric...", end='')
        sleep(0.05)
        try:
            fabricurl = requests.get('https://maven.fabricmc.net/net/fabricmc/fabric-installer/0.10.2/fabric-installer-0.10.2.jar', allow_redirects=True)
            open("fabricinstaller.jar", "wb").write(fabricurl.content)
            print(G+"DONE"+W)
            print("Installing Fabric...", end='')
            sleep(0.05)
            os.system('java -jar fabricinstaller.jar server -mcversion 1.17.1 -downloadMinecraft')     
            print(G+"DONE"+W)     
            print("Removing Fabric installer files...", end='')
            os.remove("fabricinstaller.jar")  
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
    else:
        print(R+"Skipping Fabric install... FABRIC IS A REQUIRED COMPONENT, BE SURE TO INSTALL IT MUNUALLY AFTERWARDS"+W)
        sleep(0.05)
    #dependencies
    if dependencies == 1:
        print("Downloading Dependencies "+B+"["+W)
        sleep(0.05)
        try:
            os.mkdir('mods')
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading FabricAPI...", end='')
            sleep(0.05)
            fabricapiurl = requests.get('https://github.com/FabricMC/fabric/releases/download/0.45.0%2B1.17/fabric-api-0.45.0+1.17.jar', allow_redirects=True)
            open('mods/fabric-api-0.45.0+1.17.jar', 'wb').write(fabricapiurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading Switchable Resource Packs...", end='')
            sleep(0.05)
            srpurl = requests.get('https://github.com/kyrptonaught/SwitchableResourcepacks/releases/download/1.0.0/switchableresourcepacks-1.0.0-1.17.jar', allow_redirects=True)
            open('mods/switchableresourcepacks-1.0.0-1.17.jar', 'wb').write(srpurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading Scoreboard Suffix...", end='')
            sleep(0.05)
            scoreboardsuffixurl = requests.get('https://github.com/kyrptonaught/scoreboardsuffix/releases/download/1.0.3/scoreboardsuffix-1.0.3-1.17.jar', allow_redirects=True)
            open('mods/scoreboardsuffix-1.0.3-1.17.jar', 'wb').write(scoreboardsuffixurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)            
        try:
            print("Downloading Extended Structures...", end='')
            sleep(0.05)
            extstructureurl = requests.get('https://github.com/kyrptonaught/Extended-Structures/releases/download/1.0.0/extendedstructures-1.0.0-1.17.1.jar', allow_redirects=True)
            open('mods/extendedstructures-1.0.0-1.17.1.jar', 'wb').write(extstructureurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading Take Everything...", end='')
            sleep(0.05)
            takeallurl = requests.get('https://github.com/kyrptonaught/Take-Everything/releases/download/1.0.1/takeeverything-1.0.1-1.17.1.jar', allow_redirects=True)
            open('mods/takeeverything-1.0.1-1.17.1.jar', 'wb').write(takeallurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading SnowballKB...", end='')
            sleep(0.05)
            snowballkburl = requests.get('https://github.com/capitalistspz/SnowballKB/releases/download/1.1/snowballkb-1.1-1.17.jar', allow_redirects=True)
            open('mods/snowballkb-1.1-1.17.jar', 'wb').write(snowballkburl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading Starlight...", end='')
            sleep(0.05)
            starlighturl = requests.get('https://cdn.modrinth.com/data/H8CaAYZC/versions/Starlight%201.0.0%201.17.x/starlight-1.0.0+fabric.73f6d37.jar', allow_redirects=True)
            open('mods/starlight-1.0.0+fabric.73f6d37.jar', 'wb').write(starlighturl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        print(B+"] "+W+"Dependencies "+G+"DONE"+W)
    else:
        print(R+"Skipping Dependencies... DEPENDENCIES ARE REQUIRED COMPONENTS, BE SURE TO INSTALL THEM MUNUALLY AFTERWARDS"+W)
        sleep(0.05)
    #optimize
    if optimize == 1:
        print("Downloading Optimizations "+B+"["+W)
        sleep(0.05)
        try:
            os.mkdir('mods')
        except OSError as error:
            print("", end='')
            pass
        try:
            print("Downloading FerriteCore...", end='')
            sleep(0.05)
            ferritecoreurl = requests.get('https://cdn.modrinth.com/data/uXXizFIs/versions/3.1.0/ferritecore-3.1.0-fabric.jar', allow_redirects=True)
            open('mods/ferritecore-3.1.0-fabric.jar', 'wb').write(ferritecoreurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading Hydrogen...", end='')
            sleep(0.05)
            hydrogenurl = requests.get('https://cdn.modrinth.com/data/AZomiSrC/versions/mc1.17.1-0.3.1/hydrogen-fabric-mc1.17.1-0.3.jar', allow_redirects=True)
            open('mods/hydrogen-fabric-mc1.17.1-0.3.jar', 'wb').write(hydrogenurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        try:
            print("Downloading Lithium...", end='')
            sleep(0.05)
            lithiumurl = requests.get('https://cdn.modrinth.com/data/gvQqBUqZ/versions/mc1.17.1-0.7.5/lithium-fabric-mc1.17.1-0.7.5.jar', allow_redirects=True)
            open('mods/lithium-fabric-mc1.17.1-0.7.5.jar', 'wb').write(lithiumurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
        print(B+"] "+W+"Optimizations "+G+"DONE"+W)
    else:
        print(E+"Skipping Optimizations..."+W)
        sleep(0.05)
    #upnp
    if upnp == 1:
        print("Downloading dedicatedUPnP...", end='')
        sleep(0.05)
        try:
            dedicatedmcupnpurl = requests.get('https://media.forgecdn.net/files/3525/899/dedicatedmcupnp-1.2.0.jar', allow_redirects=True)
            open('mods/dedicatedmcupnp-1.2.0.jar', 'wb').write(dedicatedmcupnpurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
    else:
        print(E+"Skipping UPnP..."+W)
        sleep(0.05)
    #viafabric
    if viafabric == 1:
        print("Downloading ViaFabric...", end='')
        sleep(0.05)
        try:
            viafabricurl = requests.get('https://media.forgecdn.net/files/3544/467/viafabric-0.4.5%2B244-main.jar', allow_redirects=True)
            open('mods/viafabric-0.4.5+244-main.jar', 'wb').write(viafabricurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
    else:
        print(E+"Skipping ViaFabric..."+W)
        sleep(0.05)
    #minimotd
    if minimotd == 1:
        print("Downloading MiniMOTD...", end='')
        sleep(0.05)
        try:
            minimotdurl = requests.get('https://cdn.modrinth.com/data/16vhQOQN/versions/2.0.4+1.17.1/minimotd-fabric-mc1.17.1-2.0.4.jar', allow_redirects=True)
            open('mods/minimotd-fabric-mc1.17.1-2.0.4.jar', 'wb').write(minimotdurl.content)
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
    else:
        print(E+"Skipping MiniMOTD..."+W)
        sleep(0.05)
    #scripts
    ram = (str(ram))
    print("Creating server scripts "+B+"["+W)
    sleep(0.05)
    print(B+"RAM...: " + G + ram + B + " GB"+W)
    print(B+"Server GUI...: " + G, end='')
    if server_scripts == 1:
        print("Yes")
    else:
        print("No")
    print(B+"MOTD Sync...: " + G, end='')
    if motd_sync == 1:
        print("Yes")
    else:
        print("No")
    try:
        if server_scripts == 1:
            scriptgui = ""
        else:
            scriptgui = "nogui"
        #Windows
        winscript = open("Run-Windows.cmd","w")
        #MacOS
        macscript = open("Run-MacOS.sh","w")
        #Linux
        linuxscript = open("Run-Linux.sh","w")
                           
        winscript.write("@ECHO OFF\njava -Xmx"+ram+"G -Xms"+ram+"G -jar fabric-server-launch.jar "+scriptgui+"\nPAUSE")
        macscript.write("exec java -Xmx"+ram+"G -Xms"+ram+"G -jar fabric-server-launch.jar "+scriptgui)
        linuxscript.write("java -Xmx"+ram+"G -Xms"+ram+"G -jar fabric-server-launch.jar "+scriptgui)

        winscript.close()
        macscript.close()
        linuxscript.close()

        #linux chmod
        if platform.system() == "Linux":
            os.system("chmod +x Run-Linux.sh")
    except OSError as error:
        print(R+"FAIL (" + str(error) + ")"+W)
    print(B+"] "+W+"Creating server scripts "+G+"DONE"+W)
    #EULA
    if eula == 1:
        print("Agreeing Minecraft EULA...", end='')
        sleep(0.05)
        try:
            eulafile = open("eula.txt","w")
            eulafile.write("eula=TRUE")
            eulafile.close()
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ")"+W)
    else:
        print(R+"Skipping Minecraft's EULA agreement... MINECRAFT'S EULA AGREEMENT IS REQUIERED, BE SURE TO SET EULA=TRUE OR IT WON'T RUN"+W)
        sleep(0.05)
    print("")
    print("")
    print(G+"*** Core-Server Setup successful! ***"+W)
    print("")
    print(E+"Preparing to install LEB-Resources in 5 seconds . . ."+W)
    sleep(5)
    prepare()
    downloadInstall()
    setMOTD()
    setLR()
    clean()
    print("")
    print(G+"***************************")
    print("*** Install successful! ***")
    print("***************************"+W)
    print("")
    action = input(B+"Press ENTER to continue . . ."+W)
    installMenu_13()


def installMenu_13():
    cls()
    print("=======================================================")
    print(G+"LEB Install Completed"+W)
    print(G+"Finish setup"+W)
    print("=======================================================")
    print("")
    print(G+"LEB has been intalled successfully!"+W)
    print("You are now ready to run your own LEB server.")
    print("To run your server, execute the file called <<Run ...>> and your OS of preference.")
    print("You can change your LEB ToolBox and server settings at the Settings page.")
    print("")
    print(B+"LEB"+W+"-"+G+"ToolBox "+W+"created by Pi"+R+"por"+O+"Games"+W)
    print(E+"Legacy Edition"+O+" Battle"+W+" created by "+R+"DBTDerpbox "+E+"+"+B+" contributors"+W)
    print("Consider donating at"+O+" Patreon"+W+"! "+O+"patreon.com/DBTDerpbox"+W)
    print("")
    action = input(B+"Press ENTER to return to the main menu . . ."+W)
    mainMenu()

    
def updateMenu():
    cls()
    print("=======================================================")
    print(G+"Update LEB"+W)
    print("=======================================================")
    print("")
    print(P+"Choose an action below:"+W)
    print("")
    print("1. Update to the latest commit available " + E +"(current commit instaled: " +G+current_hash+E+")"+W)
    print("2. Perform a Clean update to the latest commit available")
    print("3. Reinstall LEB")
    print("")
    print("4. Change branch (current selected branch: ", B+cfg_branch+W, ")")
    print("")
    print("5. Exit")
    print("")
    action = input(B+"Input: "+W)
    
    if action == "1":
        updater()
    elif action == "2":
        cleanUpdater()
    elif action == "3":
        reinstallMenu()
    elif action == "4":
        changeBranch()
    elif action == "5":
        mainMenu()
    else:
        updateMenu()


def updater():
    cls()
    print("=======================================================")
    print(G+"Update LEB > Update to the latest commit available"+W)
    print("=======================================================")
    print("")
    print("This will update you current server to the latest version (commit) uploaded to the GitHub repository.")
    print("")
    print(P+"Are you sure you want to update to the last commit?"+W)
    print("")
    action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
    if action.lower() == "y":
        print(E+"User authorised operation, executing..."+W)
         #continue execution
    elif action.lower() == "n":
        mainMenu()
    else:
        updater()
    print("")
    prepare()
    backup()
    downloadInstall()
    setMOTD()
    setLR()
    restore()
    clean()
    print()
    print(G+"*** Update successful! ***"+W)
    print("")
    action2 = input(B+"Press ENTER to continue . . ."+W)
    mainMenu()

def cleanUpdater():
    cls()
    print("=======================================================")
    print(G+"Update LEB > Perform a Clean update to the latest commit available"+W)
    print("=======================================================")
    print("")
    print(R+"WARNING!: Performing a Clean Update will erase all player data save data (ex: achievements).")
    print("It's recommended to backup playerdata to avoid loosing player-specific-settings, custom presets, achievements,...")
    print("If you are troubleshooting problems, feel free to continue." +W)
    print("")
    print(P+"Are you sure you want to ERASE everything and install again?"+W)
    print("")
    action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
    if action.lower() == "y":
        print(E+"User authorised operation, executing..."+W)
         #continue execution
    elif action.lower() == "n":
        mainMenu()
    else:
        cleanUpdater()
    print("")
    prepare()
    downloadInstall()
    setMOTD()
    setLR()
    clean()
    print()
    print(G+"*** Clean Update successful! ***"+W)
    print("")
    action2 = input(B+"Press ENTER to continue . . ."+W)
    mainMenu()

def reinstallMenu():
    cls()
    print("=======================================================")
    print(G+"Update LEB > Reinstall LEB"+W)
    print("=======================================================")
    print("")
    print(R+"WARNING!: Reinstalling LEB will erase ALL DATA, including server files, player data and LEB resources.")
    print("It's recommended to backup playerdata to avoid loosing player-specific-settings, custom presets, achievements,...")
    print("If you are troubleshooting problems, feel free to continue." +W)
    print("")
    print(P+"Are you sure you want to TRULY ERASE EVERYTHING NO JOKES and install again?"+W)
    print("")
    action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
    if action.lower() == "y":
        print(E+"User authorised operation, executing..."+W)
         #continue execution
    elif action.lower() == "n":
        mainMenu()
    else:
        cleanUpdater()
    print("")
    reinstall()
    installMenu()
    #I think this part of the code won't be executing anytime soon, but who cares.
    print()
    print(G+"*** Reinstall successful! ***"+W)
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
    writeConfig(cfg_branch,motd_sync,current_hash,check_for_updates,legacy_resetter)
    mainMenu()


def settingsMenu():
    readConfig()
    response_motd = ""
    response_cfu = ""
    response_lr = ""
    if motd_sync == 1:
        response_motd = G+"TRUE"+W
    else:
        response_motd = R+"FALSE"+W
    if check_for_updates == 1:
        response_cfu = G+"TRUE"+W
    else:
        response_cfu = R+"FALSE"+W
    if legacy_resetter == 1:
        response_lr = G+"TRUE"+W
    else:
        response_lr = R+"FALSE"+W
    cls()
    print("=======================================================")
    print(G+"Settings"+W)
    print("=======================================================")
    print("")
    print("Welcome to the Settings page, you can change your ToolBox/Server settings here.")
    print(E+"Program version: "+ver_program+W)
    print("")
    print(G+"LEB-ToolBox Settings:"+W)
    print("")
    print("1. Change branch (current selected branch: ", B+cfg_branch+W, ")")
    print(E+"   Allows you to change the build branch the server is running on."+W)
    print("2. Update LEB-ToolBox (current version: "+ver_program+W+")")
    print(E+"   Update LEB-ToolBox to the latest stable version available at GitHub."+W)
    print("3. Check for LEB-ToolBox updates at startup ("+response_cfu+")"+W)
    print(E+"   This will check if there are any updates available for LEB-ToolBox when you start the application."+W)
    print("")
    print(B+"Server Settings:"+W)
    print("")
    print("4. Use MOTD Sync ("+response_motd+")"+W)
    print(E+"   Automatically syncs the MOTD of the server with the commit version currently installed."+W)
    print("5. Use Legacy Resetter ("+response_lr+")"+W)
    print(E+"   Use alternative map resetter system; requires less resources but it's more unestable."+W)
    print("")
    print("")
    print("6. Exit")
    print("")
    print(P+"Choose an action below:"+W)
    print("")
    action = input(B+"Input: "+W)
    if motd_sync == 1:
        response_motd = 1
    else:
        response_motd = 0
        
    if check_for_updates == 1:
        response_cfu = 1
    else:
        response_cfu = 0

    if legacy_resetter == 1:
        response_lr = 1
    else:
        response_lr = 0
        
    if action == "1":
        changeBranch()
    elif action == "2":
        updateLEBTB()
    elif action == "3":
        print("")
        if response_cfu == 1:
            writeConfig(cfg_branch,motd_sync,current_hash,"0",legacy_resetter)
            print("CFU at startup has been "+R+"disabled"+W+" successfully.")
        else:
            writeConfig(cfg_branch,motd_sync,current_hash,"1",legacy_resetter)
            print("CFU at startup has been "+G+"enabled"+W+" successfully.")   
        print("")
        sleep(2)
        settingsMenu()
    elif action == "4":
        print("")
        if response_motd == 1:
            writeConfig(cfg_branch,"0",current_hash,check_for_updates,legacy_resetter)
            print("MOTD Sync has been "+R+"disabled"+W+" successfully.")
        else:
            writeConfig(cfg_branch,"1",current_hash,check_for_updates,legacy_resetter)
            print("MOTD Sync has been "+G+"enabled"+W+" successfully.")
            print(W+"To apply the changes, you must <Update to the last commit> from the Update page."+W)
        print("")
        sleep(3)
        settingsMenu()
    elif action == "5":
        print("")
        if response_lr == 1:
            writeConfig(cfg_branch,motd_sync,current_hash,check_for_updates,"0")
            print("Legacy Resetter has been "+R+"disabled"+W+" successfully.")
        else:
            writeConfig(cfg_branch,motd_sync,current_hash,check_for_updates,"1")
            print("Legacy Resetter has been "+G+"enabled"+W+" successfully.")   
        print("")
        readConfig()
        setLR()
        print("")
        sleep(2)
        settingsMenu()
    elif action == "6":
        mainMenu()
    else:
        settingsMenu()


def updateLEBTB():
    cls()
    print("=======================================================")
    print(G+"Update LEB-ToolBox"+W)
    print("=======================================================")
    print("")
    print("You can update your LEB-ToolBox program to the latest stable version available at GitHub.")
    print("The current LEB-ToolBox program version is "+ver_program+W)
    print("Newer LEB-ToolBox updates might ship with new features, bugfixes, and other improvements!")
    print("")
    print(E+"Searching for updates . . ."+W)
    print("")
    result = checkForUpdates()
    result2 = checkForChangeLog()
    cls()
    print("=======================================================")
    print(G+"Update LEB-ToolBox"+W)
    print("=======================================================")
    print("")
    print("You can update your LEB-ToolBox program to the latest stable version available at GitHub.")
    print("The current LEB-ToolBox program version is "+ver_program+W)
    print("Newer LEB-ToolBox updates might ship with new features, bugfixes, and other improvements!")
    print("")
    if result == 0:
        print(E+"There aren't any new updates. Your program is fully updated.")
        print("")
        action = input(B+"Press ENTER to return . . ."+W)
        settingsMenu()
    elif result == -1:
        print(R+"An unknown error has occurred while fetching the required information.")
        print("")
        action = input(R+"Press ENTER to return . . ."+W)
        settingsMenu()
    elif result == 1:
        print(G+"New update v"+str(online_count_program)+" available!")
        print("")
        print(B+"CHANGELOG:"+W)
        print(result2)
        print("")
        print(P+"Do you want to update now?"+W)
        print("")
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
        if action.lower() == "y":
            ### LEB-ToolBox update
            print(E+"Preparing to update LEB-ToolBox..."+W)
            print("Downloading LEB-ToolBox...", end="")
            pgrmfile = ""
            try:
                if platform.system() == "Linux":
                    prgrmfile = requests.get('https://raw.githubusercontent.com/DBTDerpbox/LEB-ToolBox/main/LEB-ToolBox', allow_redirects=True)
                    open("LEB-ToolBox-new", "wb").write(prgrmfile.content)
                elif platform.system() == "Darwin":
                    prgrmfile = requests.get('https://raw.githubusercontent.com/DBTDerpbox/LEB-ToolBox/main/LEB-ToolBox', allow_redirects=True)
                    open("LEB-ToolBox-new", "wb").write(prgrmfile.content)
                elif platform.system() == "Windows":
                    prgrmfile = requests.get('https://raw.githubusercontent.com/DBTDerpbox/LEB-ToolBox/main/LEB-ToolBox.exe', allow_redirects=True)
                    open("LEB-ToolBox-new.exe", "wb").write(prgrmfile.content)
                print(G+"DONE"+W)
            except Exception as error:
                print(R+"FAIL ("+str(error)+")"+W)
            print(P+"Do you want to keep the old version (in case the newer version breaks)?"+W)
            print("")
            action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
            try:
                if action.lower() == "y":
                    print("Keeping old version...", end="")
                    if platform.system() == "Linux":
                        os.rename("LEB-ToolBox", "LEB-ToolBox_old")
                    elif platform.system() == "Darwin":
                        os.rename("LEB-ToolBox", "LEB-ToolBox_old")
                    elif platform.system() == "Windows":
                        os.rename("LEB-ToolBox.exe", "LEB-ToolBox_old.exe")
                else:
                    print("Marking DELETE_ME to old version...", end="")
                    if platform.system() == "Linux":
                        os.rename("LEB-ToolBox", "LEB-ToolBox_DELETE_ME")
                    elif platform.system() == "Darwin":
                        os.rename("LEB-ToolBox", "LEB-ToolBox_DELETE_ME")
                    elif platform.system() == "Windows":
                        os.rename("LEB-ToolBox.exe", "LEB-ToolBox_DELETE_ME.exe")
                    print(G+"DONE"+W)
            except Exception as error:
                print(R+"FAIL ("+str(error)+")"+W)
            try:
                print("Renaming new update file...", end="")
                if platform.system() == "Linux":
                    os.rename("LEB-ToolBox-new", "LEB-ToolBox")
                    os.system("chmod +x LEB-ToolBox")
                elif platform.system() == "Darwin":
                    os.rename("LEB-ToolBox-new", "LEB-ToolBox")
                elif platform.system() == "Windows":
                    os.rename("LEB-ToolBox-new.exe", "LEB-ToolBox.exe")
            except Exception as error:
                print(R+"FAIL ("+str(error)+")"+W)
            print("")
            print(O+"***" +W+"LEB-ToolBox will restart in 5 . . ."+O+" ***"+W)
            print("")
            sleep(5)
            print(E+"Restarting..."+W)
            if platform.system() == "Linux":
                os.system("./LEB-ToolBox")
            elif platform.system() == "Darwin":
                os.system("./LEB-ToolBox")
            elif platform.system() == "Windows":
                os.startfile("LEB-ToolBox.exe")
            exit()
        elif action.lower() == "n":
            settingsMenu()
        else:
            updateLEBTB()


def changeLog():
    cls()
    print("=======================================================")
    print(B+"CHANGELOG"+W)
    print("=======================================================")
    print("")
    print("You are now watching the LEB-ToolBox "+ver_program+" "+B+"changelog"+W+".")
    print("")
    print(B+"CHANGELOG:"+W)
    print(ver_info)
    print("")
    print("")
    action = input(B+"Press ENTER to return . . ."+W)
    mainMenu()
            

    
####################
###   Functions  ###
####################
def writeConfig(var_branch,var_motd_sync,var_hash,var_cfu,var_lr):
    try:
        f = open("updater.cfg", "w")
        f.write(var_branch+"#/#"+str(var_motd_sync)+"#/#"+var_hash+"#/#"+str(var_cfu)+"#/#"+str(var_lr))
    finally:
        f.close()

def prepare():
    if lebDebugKeepCache == 0:
        print("Preparing files...", end='')
        sleep(0.05)
        try:
            shutil.rmtree("leb_update_cache")
            os.mkdir("leb_update_cache")
        except OSError as error:
            os.mkdir("leb_update_cache")
            print("", end='')
        finally:
            print(G+"DONE"+W)

def backup():
    print("Backing up...", end='')
    sleep(0.05)      
    try:
        shutil.copytree('world/advancements', 'leb_update_cache/world/advancements')
    except OSError as error:
        print(error, end='')
        pass
    finally:
        print(G+"DONE"+W)


def downloadInstall():
    if lebDebugDisableDownloadContent == 0:
        print(E+"Note: Due to GitHub limitations, download ETA is not available."+W)
        print("Downloading build" + E+ " (this can take up to 6 minutes)" + W + "...", end='')
        leb_zip = requests.get('https://github.com/DBTDerpbox/Legacy-Edition-Battle/archive/refs/heads/' + cfg_branch+ '.zip', allow_redirects=True)
        open("leb_update_cache/leb.zip", "wb").write(leb_zip.content)
        print(G+"DONE"+W)
    print("Removing old files "+B+"["+W)
    sleep(0.05)
    files = [".gitignore","INSTALLATION.md","INSTALLATION-MINEHUT.md","LICENSE","README.md","SCREENSHOTS.md","CUSTOMPACK.md"]
    directories = ["world","images","config",".github"]
    try:
        #crear arrays uno de archivos y otro de carpetas y hacer loop
        for file in files:
            if os.path.isfile(file):
                print ("Removing " + str(file) + " ...", end='')
                os.remove(file)
                print(G+"DONE"+W)
        for directory in directories:
            if os.path.isdir(directory):
                print ("Removing " + str(directory) + " ...", end='')
                shutil.rmtree(directory)
                print(G+"DONE"+W)
    except Exception as error:
        print (R+"FAIL ("+str(error)+"), stopping code..."+W, end='')
        pass
    print(B+"] "+W+"Removing old files "+G+"DONE"+W)
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
    except Exception as error:
        str(error)
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
        print("", end='')
        pass
    finally:
        print(G+"DONE"+W)
        
    
def clean():
    if lebDebugKeepCache == 0:
        try:
            shutil.rmtree("leb_update_cache")
        except OSError as error:
            print(R+error, end='')
            pass
        finally:
            print(G+"DONE"+W)


def setMOTD():
    if motd_sync == 1:
        try:
            print("Syncing MOTD...", end='')
            #get hash
            leb_zip = ZipFile('leb_update_cache/leb.zip')
            git_hash = "git-" + cfg_branch + "-" + leb_zip.comment.decode("utf-8")[:6]

            #read contents
            with open("server.properties", "r") as motd_file:
                lines = motd_file.readlines()
            with open("server.properties", "w") as motd_file:
                for line in lines:
                    if "motd=" not in line:
                        motd_file.write(line)
                motd_file.write("\nmotd=\u00A79Legacy Edition Battle Public Server \u00A7r\u00A7r\\n" + git_hash)
            motd_file.close()

            minimotd_file = open("config/MiniMOTD/main.conf", "r")
            content = minimotd_file.read()
            old_motd = stringTF("<gradient:#d8d8d8:#2bc7ac><italic>","</gradient>",content)
            new_motd = content.replace(old_motd, git_hash)
            minimotd_file.close()
            
            minimotd_file = open("config/MiniMOTD/main.conf", "w")
            minimotd_file.write(new_motd)
            minimotd_file.close()
            print(G+"DONE"+W)
        except OSError as error:
            print(R+"FAIL (" + str(error) + ") >>> Did leb_update_cache/leb.zip erase itself, or does this branch not contain a miniMOTD/server.properties config file?"+W)
            pass
        finally:
            writeConfig(cfg_branch,motd_sync,leb_zip.comment.decode("utf-8")[:6],check_for_updates,legacy_resetter)

def setLR():
    try:
        if legacy_resetter == 1:
            print("Patching Legacy Resetter fix...", end='')         
        with open("world/datapacks/4jbattle/data/4jbattle/functions/start.mcfunction", "r") as lr_file:
            lines = lr_file.readlines()
        with open("world/datapacks/4jbattle/data/4jbattle/functions/start.mcfunction", "w") as lr_file:
            for line in lines:
                if "scoreboard players set #Store 4j.legacyreset 1" not in line:
                    lr_file.write(line)
            if legacy_resetter == 1:
                lr_file.write("\nscoreboard players set #Store 4j.legacyreset 1")
        lr_file.close()
        if legacy_resetter == 1:
            print(G+"DONE"+W)
    except OSError as error:
        print(R+"FAIL (" + str(error) + ")"+W)
        pass

def reinstall():
    print("Removing "+R+"ALL FILES"+B+" ["+W)
    sleep(0.05)
    files = [".gitignore","INSTALLATION.md","INSTALLATION-MINEHUT.md","LICENSE","README.md","SCREENSHOTS.md","CUSTOMPACK.md","installer.py","banned-ips.json","banned-players.json","eula.txt","fabric-server-launch.jar","fabric-server-launcher.properties","ops.json","Run-Linux.sh","Run-MacOS.sh","Run-Windows.cmd","server.jar","server.properties","usercache.json","whitelist.json"]
    directories = ["world","images","config",".github",".fabric","libraries","logs","mods"]
    try:
        for file in files:
            if os.path.isfile(file):
                print ("Removing " + str(file) + " ...", end='')
                os.remove(file)
                print(G+"DONE"+W)
        for directory in directories:
            if os.path.isdir(directory):
                print ("Removing " + str(directory) + " ...", end='')
                shutil.rmtree(directory)
                print(G+"DONE"+W)
    except Exception as error:
        print (R+"FAIL ("+str(error)+"), stopping code..."+W, end='')
        pass
    print(B+"] "+W+"Removing "+R+"ALL FILES "+G+"DONE"+W)
    sleep(2)


def checkForUpdates():
    try:
        req = requests.get('https://raw.githubusercontent.com/DBTDerpbox/LEB-ToolBox/main/LEB-ToolBox.py', allow_redirects=True)
        data = (req.content).decode("utf-8")
        info = data.split("\n")
        for line in info:
            if "cnt_program = " in line:
                ver1 = float(line.split("cnt_program = ")[1])
                if ver1 > cnt_program:
                    global online_count_program
                    online_count_program = ver1
                    return 1
                else:
                    return 0
    except Exception as error:
        return -1
    
def checkForChangeLog():
    try:
        req = requests.get('https://raw.githubusercontent.com/DBTDerpbox/LEB-ToolBox/main/LEB-ToolBox.py', allow_redirects=True)
        data = (req.content).decode("utf-8")
        info = data.split("\n")
        for line in info:
            if "ver_info = " in line:
                changelog = float(line.split("ver_info = ")[1])
                return changelog
    except Exception as error:
        return R+"Error when retrieving changelog."

def rmOldVer():
    if os.path.isfile('LEB-ToolBox_DELETE_ME.exe'):
        try:
            os.remove("LEB-ToolBox_DELETE_ME.exe")
        except Exception as error:
            print(R+str(error)+W)
            print("")
            sleep(3)
            pass
    if os.path.isfile('LEB-ToolBox_DELETE_ME'):
        try:
            os.remove("LEB-ToolBox_DELETE_ME")
        except Exception as error:
            print(R+str(error)+W)
            print("")
            sleep(3)
            pass


#pre-initialization check for updates (cfu) routine
rmOldVer()
readConfig()
cls()
if check_for_updates == 1:
    result = checkForUpdates()
    result2 = checkForChangeLog()
    if result == 1:
        print(O+"*******************************************************"+W)
        print(G+"New LEB-ToolBox v"+str(online_count_program)+" update has been found!"+W)
        print(O+"*******************************************************"+W)
        print("")
        print(B+"CHANGELOG:"+W)
        print(result2)
        print("")
        print(P+"Do you want to go to the LEB-ToolBox update Menu?"+W)
        print("")
        action = input(B+"Input " + G + "[Y/N]" + B + ": "+W)
        if action.lower() == "y":
            updateLEBTB()

# The one line of code that makes this all work #
mainMenu()

########################################################
###  Tool created by PiporGames, with love, for LEB  ###
########################################################
