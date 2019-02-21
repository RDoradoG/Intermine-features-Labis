import sys, getopt, os, argparse, re, subprocess, time

class HCCAProcess(object):
    """docstring for HCCAProcess"""
    def __init__(self, arg):
        self.folder           = arg.folder
        self.start_file       = arg.start_file
        self.hrr_folder       = arg.hrr_folder
        self.cpu              = arg.cpu
        self.hrr_cut_file     = arg.hrr_cut_file
        self.hcca_python_file = arg.hcca_python_file
        self.max_hrr          = arg.max_hrr
        self.step             = arg.step
        self.min              = arg.min
        self.max              = arg.max
        self.min_isl          = arg.min_isl
        self.no_file          = arg.no_file.split(',')
        self.tmp_id_files     = "tmp_id_files"
        self.tmp_folder       = self.folder + 'HCCA_tmp'
        self.new_file         = 'expression_values.txt'

    def execute(self):
        self.expression_values_files = self.getExpressionValuesFiles()
        self.createTmpFolder(self.tmp_folder)
        self.getSortedFiles()
        self.getHedaersOfFile()
        self.joinFiles()
        self.expressionValues_file   = self.joinHeadersFile()
        
        #### checking lines ####
        self.cutInLine(8001)
        ########################
        
        self.getIds()
        self.RunMpirun()
        parameters                   = self.calculateParametes()
        self.HRRCut(parameters)
        self.getHCCA()
        self.copyAndRemove(self.tmp_folder)

    def copyAndRemove(Self, oldFolder):
        cmdline('mv Result.HCCA ../Clusters_Expression_values.HCCA')
        os.chdir('..')
        cmdline('rm -r ' + oldFolder)

    def getHCCA(self):
        cmdline('python2 ' + self.hcca_python_file + ' -f Result.hrr -s ' + self.step + ' --min ' + self.min + ' --max ' + self.max + ' -m ' + self.max_hrr + ' -i ' + self.min_isl)

    def HRRCut(self, parameters):
        cmdline('Rscript ' + self.hrr_cut_file + ' -n ' + self.cpu + ' -a ' + self.tmp_id_files + ' -s ' + str(parameters['s']) + ' -S ' + str(parameters['S']) + ' -c ' + str(parameters['c']) + ' -r HRR -m ' + self.cpu + ' -z ' + self.max_hrr)

    def getIds(self):
        cmdline("awk -F'\t' '{print $1}' " + self.expressionValues_file + " | sed 1d > " + self.tmp_id_files)

    def calculateParametes(self):
        cpu             = int(self.cpu)
        lines           = self.getNumberLines(self.expressionValues_file)
        parameters      = {'s': 0, 'S': 0, 'c': 0}
        parameters['S'] = lines - 1
        while ((lines % cpu) != 0):
            lines += 1
        parameters['s'] = lines
        parameters['c'] = int(parameters['s'] / cpu)
        return parameters

    def cutInLine(self, maxLine):
        lines = self.getNumberLines(self.expressionValues_file)
        if int(lines) > maxLine:
            cmdline("cat " + self.expressionValues_file + ' | head -n ' + str(maxLine) + ' > tmp_file_tmp')
            cmdline("cat tmp_file_tmp > " + self.expressionValues_file)
            cmdline('rm tmp_file_tmp')

    def RunMpirun(self):
        a = cmdline('mpirun -np ' + str(self.cpu) + ' ' + self.hrr_folder + ' ' + self.expressionValues_file + ' rankmatrix')

    def getExpressionValuesFiles(self):
        listFiles               = ll(self.folder)
        expression_values_files = []

        for file in listFiles:
            if file.startswith(self.start_file):
                if not file in self.no_file:
                    expression_values_files.append(file)

        return expression_values_files

    def createTmpFolder(self, newFolder):
        cmdline("mkdir " + newFolder)
        os.chdir(newFolder)

    def getHedaersOfFile(self):
        for file in self.expression_values_files:
            cmdline("cat " + self.folder + file + " | head -n 1 | cut -f 4- > Head-" + file)

        cmdline("cat Head-* | sed ':a;N;$!ba;s/\\n/\\t/g' | sed '1s/^/\\t/' > Headers.txt")
        self.removeFile('Head-*')


    def getSortedFiles(self):
        for file in self.expression_values_files:
            cmdline("cat " + self.folder + file + " | sed 1d | cut -f 1,4- | sort -k1 > Sort-" + file)

    def joinHeadersFile(self):
        cmdline("cat Headers.txt joined_file.txt > " + self.new_file)
        return self.new_file

    def joinFiles(self):
        not_yet      = True
        result_file  = "joined_file.txt"
        result_file2 = "tmp_joined_file.txt"
        suma         = 0
        for file in self.expression_values_files:
            if not_yet:
                tmp_result_file = "Sort-" + file
                N_col_1         = self.getNumberColumns(tmp_result_file)
                Str_cols_1      = self.getSTRCol('', 1, N_col_1 + 1, 1)
                not_yet         = False
                continue

            tmp_file_2      = "Sort-" + file
            N_col_2         = self.getNumberColumns(tmp_file_2)
            Str_cols_2      = self.getSTRCol('', 2, N_col_2 + 1, 2)
            
            command         = 'join -j 1 -o ' + Str_cols_1 + ',' + Str_cols_2 + " -t '\t' " + tmp_result_file + ' ' + tmp_file_2 + ' > ' + result_file2
            cmdline(command)
            cmdline('cat ' + result_file2 + ' > ' + result_file)
            Str_cols_1      = self.getSTRCol(Str_cols_1, N_col_1 + 1, N_col_1 + N_col_2, 1)
            N_col_1         = N_col_1 + N_col_2 - 1
            tmp_result_file = result_file

        self.removeFile(result_file2)
        for file in self.expression_values_files:
            self.removeFile("Sort-" + file)

    def getSTRCol(self, res, init, end, file_number):
        for i in range(init, end):
            if res == '':
                res = str(file_number) + '.' + str(i)
            else:
                res = res + ',' + str(file_number) + '.' + str(i)
        return res

    def getNumberLines(self, file):
        lines = cmdline("wc -l " + self.expressionValues_file)
        lines = str(lines).replace(' ' + self.expressionValues_file, '').replace("\\n'", '').replace("b'", '')
        return int(lines)

    def getNumberColumns(self, file):
        result = cmdline("awk -F'\\t' '{print NF; exit}' " + file)
        return int(result)

    def removeFile(self, file):
        cmdline("rm " + file)
    
'''
Get the list of directories and files of a directory
@Param  {String}    directory   The directory to get all the files and directories
@return {array}                 List of directories and files
'''
def ll(directory):
    return os.listdir(directory)

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



def getArguments():
    """Get the arguments of the command executed
    
    Get the arguments and their values of the program execution
    
    Returns:
        Object -- The arguments with values
    """
    #python ../../hrrSoft/HCCAProcess.py -f /home/rdorado/Chlamy/expression_values/ -c 4 -m 15 -s 3 --min 10 --max 50 -i 5 -n D-ExpressionValues-Miller_2010_Pro.txt
    parser = argparse.ArgumentParser()
    parser.add_argument("-f", "--folder", help = "The name of the exppression values file")
    parser.add_argument("-c", "--cpu", help = "Number of CPUs to use; relies on 'Parallel' R package")
    parser.add_argument("-m", "--max-hrr", help = "Max hrr rank per node")
    parser.add_argument("-s", "--step", help = "The number of max step inside a cluster")
    parser.add_argument("--min", help = "Minimum of elements in a cluster")
    parser.add_argument("--max", help = "Maximum of elements in a cluster")
    parser.add_argument("-i", "--min-isl", help = "Minimum of elements in islands")
    parser.add_argument("--start-file", help = "The start of the expression values files")
    parser.add_argument("--hrr-folder", help = "Hrr folder")
    parser.add_argument("-n", "--no-file", help = "Hrr folder")
    parser.add_argument("--hrr-cut-file", help = "Hrr folder")
    parser.add_argument("--hcca-python-file", help = "Hrr folder")

    data   = parser.parse_args()

    if (isEmpty(data.folder)):
        print("Is required the expression values folder")
        parser.print_help()
        exit()

    if (not data.folder.endswith('/')):
        data.folder = data.folder + '/'

    if (isEmpty(data.cpu)):
        data.cpu = 2

    if (isEmpty(data.step)):
        data.step = 3

    if (isEmpty(data.min)):
        data.min = 40

    if (isEmpty(data.max)):
        data.max = 200

    if (isEmpty(data.max_hrr)):
        data.max_hrr = 30

    if (isEmpty(data.min_isl)):
        data.min_isl = 200

    if (isEmpty(data.hrr_folder)):
        data.hrr_folder = '/home/rdorado/Chlamy/Scripts/hrrSoft/hrr/hrr'

    if (isEmpty(data.hrr_cut_file)):
        data.hrr_cut_file = '/home/rdorado/Chlamy/Scripts/hrrSoft/HRR_cut.R'

    if (isEmpty(data.hcca_python_file)):
        data.hcca_python_file = '/home/rdorado/Chlamy/Scripts/hrrSoft/HCCA.py'

    if (isEmpty(data.start_file)):
        data.start_file = 'D-'

    if (isEmpty(data.no_file)):
        data.no_file = ''

    return data

def isEmpty(var, con = True):
    return var == None or var == '' or len(var) == 0

arguments = getArguments()

hccaclass = HCCAProcess(arguments)
hccaclass.execute()