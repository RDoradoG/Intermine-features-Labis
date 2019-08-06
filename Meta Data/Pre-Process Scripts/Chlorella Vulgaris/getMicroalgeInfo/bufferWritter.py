import sys

class bufferWritter(object):
    """docstring for bufferWritter"""
    def __init__(self, file):
        self.bufferWritter = ''
        self.bufferCount   = 0
        self.bufferMax     = 10
        self.activeFile    = ''
        self.setActiveFile(file)

    def setActiveFile(self, file):
        self.bufferCount   = 0
        self.bufferWritter = ''
        self.activeFile    = file

    def printLine(self, line):
        self.bufferWritter += line
        self.bufferCount   += 1
        if self.bufferCount == self.bufferMax:
            self.printBuffer()

    def printBuffer(self):
        with open(self.activeFile, 'a') as activeFile:
            activeFile.write(self.bufferWritter)
            
        self.bufferCount   = 0
        self.bufferWritter = ''

    def isEmpty(self):
        if self.bufferWritter == '':
            return True
        return False

    def emptyBuffer(self):
        if not self.bufferWritter == '':
            self.printBuffer()
            self.bufferWritter = ''

    def finishBuffering(self):
        self.emptyBuffer()