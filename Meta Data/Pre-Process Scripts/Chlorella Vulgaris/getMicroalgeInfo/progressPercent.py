class progressPercent(object):
    """docstring for progressPercent"""
    def __init__(self, dec):
        self.setNumberOfDecimals(dec)
        self.previousMessage = ''
        self.laterMessage    = ''

    def setNumberOfDecimals(self, num):
        self.decNum = num

    def setPreviousMesssage(self, message):
        self.previousMessage = message

    def setLaterMesssage(self, message):
        self.laterMessage = message

    def calculatePercent(self, progress, top):
        percentadvance  = (progress * 100) / top
        self.printMessage(self.previousMessage + str(self.roundNumber(percentadvance)) + "% " + self.laterMessage)

    def finishPercent(self):
        self.printMessage('')
        print(self.previousMessage + "100% " + self.laterMessage)

    def printMessage(self, percentMessage):
        numSpaces = len(self.previousMessage) + len(self.laterMessage) + self.decNum + 5
        spaces    = ' '
        for i in range(numSpaces):
            spaces += ' '

        print(spaces, end="\r")
        print(percentMessage, end="\r")

    def roundNumber(self, num):
        div = 10**self.decNum
        return int(num * div) / div