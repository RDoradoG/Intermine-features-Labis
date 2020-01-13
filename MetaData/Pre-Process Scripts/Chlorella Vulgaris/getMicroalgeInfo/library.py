import sys, os, subprocess

from six.moves.urllib.request import urlopen

def isEmpty(var, con = True):
    return var == None or var == '' or len(var) == 0

def executeURL(link):
    writeLog("get: " + link)
    try:
        contentURL = urlopen(link)
        response = {
            'response': contentURL,
            'status': True
        }

    except:
        response = executeURLTry(link, 2)

    if response['status']:
        content  = response['response'].read()
        return content.decode("utf-8")

    else:
        return ''

def executeURLTry(link, tent):
    writeLog("get tent(" + str(tent) + "): " + link)
    if tent < 4:
        try:
            content =  urlopen(link)
            return {
                'response': content,
                'status': True
            }

        except:
            return executeURLTry(link, tent + 1)

    else:
        writeLog("error getting: " + link)
        return {
            'response': {},
            'status': False
        } 

def roundNumber(num, dec):
    div = 10**dec
    return int(num * div) / div

def isEmptyObject(dictionary):
    for element in dictionary:
        if element:
            return False

    return True

def writeLog(message):
	with open('log.log', 'a') as logFile:
		logFile.write(message + '\n')

'''
Get the list of directories and files of a directory
@Param  {String}    directory   The directory to get all the files and directories
@return {array}                 List of directories and files
'''
def ll(directory):
    return os.listdir(directory)

def createFolder(newFolder):
    if not os.path.isdir(newFolder):
        cmdline("mkdir " + newFolder)

'''
Execute a command in the linux terminal
@Param  {String}    command Command to execute
@return {String}            Result of the execution
'''
def cmdline(command):
    print(command)
    process = subprocess.Popen(
        args   = command,
        shell  = True,
        stdout = subprocess.PIPE
    )

    return process.communicate()[0]

def getFolderofFile(file):
	parts = file.split("/")
	folder = ''
	for i in range(len(parts) - 1):
		folder += parts[i] + '/'

	return folder