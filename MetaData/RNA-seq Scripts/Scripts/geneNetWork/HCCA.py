import sys, getopt, os, argparse, re

from library import *
from progressPercent import progressPercent

class HCCA(object):
    """docstring for HCCA"""
    def __init__(self, hrrFile, hccaFile, step, Amin, Amax, maxHrr, minIsl, cluster_ini, island_ini, lonely_ini):
        self.hrrFile           = hrrFile
        self.step              = int(step)
        Amin                   = int(Amin)
        Amax                   = int(Amax)
        self.maxHrr            = int(maxHrr)
        self.minIsl            = int(minIsl)
        self.sizeRange         = [Amin,Amax]
        self.progressPercent   = progressPercent(2)
        self.hccaFile          = hccaFile
        self.cluster_ini       = int(cluster_ini)
        self.island_ini        = int(island_ini)
        self.lonely_ini        = int(lonely_ini)

    def execute(self):
        ##This script is written by Marek Mutwil.
        ##This script contains implementation of HCCA algorithm.
        hrr_file      = open(self.hrrFile, "r").readlines()
        self.scoreDic = {} 
        self.curDic   = {}   
        loners        = []   
        for i in range(len(hrr_file)):   ###this loop processess the network file into 2 dictionaries, self.curDic and self.scoreDic. 
            splitted    = hrr_file[i].strip().split("\t")   
            dicto       = {}
            connections = []
            seed        = splitted[0]
            for j in range(1, len(splitted)):
                if "+" in splitted[j]:
                    splitx = splitted[j].split("+")
                    if float(splitx[1]) < self.maxHrr:
                        dicto[splitx[0]] = 1 / (float(splitx[1]) + 1)
                        connections.append(splitx[0])

            if len(dicto) != 0:
                self.scoreDic[str(seed)] = dicto
                self.curDic[str(seed)]   = connections

            else:
                loners.append(str(seed))
        
        self.clustered         = []
        self.clustets          = []
        self.LeftOversNoResult = []
        notLoners      = list(self.curDic.keys())
        for i in range(len(notLoners)): ##this loop calls self.clustettes function to extract and remove nodes forming small islands
            self.clustettes([notLoners[i]])

        for i in range(len(self.clustets)): ##this loop removes nodes found by self.clustettes from the main function
            for j in range(len(self.clustets[i])):
                del self.curDic[self.clustets[i][j]]
        
        iteration = 1
        response  = self.CCA()
        while response:
            print("iteration: " + str(iteration))
            response  = self.CCA()
            iteration += 1

        mode      = False
        leftovers = list(self.curDic.keys())
        self.filler(leftovers)
        save      = []
        hcca_file = open(self.hccaFile, "a")
        for i in range(len(self.clustered)):
            for j in range(len(self.clustered[i])):
                hcca_file.write(self.clustered[i][j] + "\t" +  str(i + self.cluster_ini) + "\n")

        for i in range(len(self.clustets)):
            for j in range(len(self.clustets[i])):
                hcca_file.write(self.clustets[i][j] + "\ts" + str(i + self.island_ini) + "\n")

        for i in range(len(self.LeftOversNoResult)):
            hcca_file.write(self.LeftOversNoResult[i] + "\tLONR" + str(i) + "\n")

        for i in range(len(loners)):
            hcca_file.write(loners[i] + "\tlo" + str(i + self.lonely_ini) + "\n")

        hcca_file.close()

    def clustettes(self, lista):   ##this recursive function extracts isles of nodes that are smaller than...
        cons = []
        for j in range(len(lista)):
            cons += self.curDic[lista[j]]

        cons = list(set(cons + lista))
        if len(cons) > self.minIsl:
            return

        elif len(cons) == len(lista): 
            cons.sort()
            if cons not in self.clustets:
                self.clustets.append(cons)

            return

        else:
            self.clustettes(cons)

    def filler(self, LeftOvers): ##This function assigns nodes that were not clustered by HCCA to clusters they are having highest connectivity to.
        conScoreMat = [[]] * len(self.clustered)
        clustera    = []
        iniLen = len(LeftOvers)
        if len(LeftOvers) != 0:
            for i in range(len(LeftOvers)):
                for j in range(len(self.clustered)):
                    connections = list(set(self.scoreDic[LeftOvers[i]].keys()) & set(self.clustered[j]))
                    score       = 0
                    for k in range(len(connections)):
                        score += self.scoreDic[LeftOvers[i]][connections[k]]

                    conScoreMat[j] = score
                
                topScore = max(conScoreMat)
                if topScore != 0:
                    sizeList = []
                    for j in range(len(conScoreMat)):
                        if conScoreMat[j] == topScore:
                            sizeList.append([len(self.clustered[j]), j])

                    sizeList.sort()
                    self.clustered[sizeList[0][1]] = self.clustered[sizeList[0][1]] + [LeftOvers[i]]
                    clustera.append(LeftOvers[i])

            LeftOvers = list(set(LeftOvers) - set(clustera))
            if len(LeftOvers) == iniLen:
                self.LeftOversNoResult = LeftOvers
                return

            return self.filler(LeftOvers)

        else:
            return

    def SurroundingStep(self, lista, whole,step):  ##this function generates NVN of n steps
        if step < self.step:
            nvn = lista            
            for j in range(len(lista)):
                nvn += self.curDic[lista[j]]

            nvn = list(set(nvn))
            self.SurroundingStep(nvn, whole, step + 1)            

        else:
            whole.append(lista)
    
    def Chisel(self, NVN, clusters): ##this function recursively removes nodes from NVN. Only nodes that are connected more to the inside of NVN are retained
        temp = []
        seta = set(NVN)
        for i in range(len(NVN)):
            connections = self.curDic[NVN[i]]
            inside      = set(NVN) & set(connections)
            outside     = (set(connections) - set(inside))
            inScore     = 0
            outScore    = 0
            for j in inside:
                inScore += self.scoreDic[NVN[i]][j]

            for j in outside:
                outScore += self.scoreDic[NVN[i]][j]

            if inScore > outScore:
                temp.append(NVN[i])

        if len(temp) == len(seta):
            clusters.append(temp)
            return

        else:
            self.Chisel(temp,clusters)
        
    def BiggestIsle(self, lista, clusterSet,curSeed): ##sometimes the NVN is split into to islands after chiseling. This function finds the biggest island and keeps it. The smaller island is discarded. 
        temp = []
        for k in range(len(lista)):
            temp += self.scoreDic[lista[k]].keys()

        nodes = set(temp + lista) & clusterSet
        if len(set(nodes)) == len(set(lista)):
            curSeed.append(list(set(nodes)))
            return

        else:
            self.BiggestIsle(list(nodes), clusterSet,curSeed)

    def nonOverlappers(self, clusters): ##This function accepts a list of Stable Putative Clusters and greedily extracts non overlapping clusters with highest modularity.
        rankedClust = []
        for i in range(len(clusters)):
            inScore  = 0
            outScore = 0
            for j in range(len(clusters[i])):
                connections = set(self.scoreDic[clusters[i][j]].keys())
                inCons      = list(connections&set(clusters[i]))
                outCons     = list(connections-set(clusters[i]))
                inScore     = 0
                outScore    = 0
                for k in range(len(inCons)):
                    inScore += self.scoreDic[clusters[i][j]][inCons[k]]

                for k in range(len(outCons)):
                    outScore += self.scoreDic[clusters[i][j]][outCons[k]]

            rankedClust.append([outScore / inScore, clusters[i]])

        if isEmpty(rankedClust):
            return []
            
        rankedClust.sort()
        BestClust = [rankedClust[0][1]]
        for i in range(len(rankedClust)):
            counter = 0
            for j in range(len(BestClust)):
                if len(set(rankedClust[i][1]) & set(BestClust[j])) > 0:
                    counter += 1
                    break

            if (counter == 0) and (rankedClust[i][0] < 1):
                BestClust.append(rankedClust[i][1])

        return BestClust

    def networkEditor(self, clustered): ##This function removes nodes in accepted clusters from the current network.
        connected      = []
        clusteredNodes = []
        for i in range(len(clustered)):
            clusteredNodes += clustered[i]
            for j in range(len(clustered[i])):
                connected += self.curDic[clustered[i][j]]
                del self.curDic[clustered[i][j]]

        connections = list(set(connected) - set(clusteredNodes))
        for i in range(len(connections)):
            self.curDic[connections[i]] = list(set(self.curDic[connections[i]]) - set(clusteredNodes))

    def CCA(self):  ##This function initiates CCA functions
        save         = []
        notClustered = list(self.curDic.keys())
        self.progressPercent.setPreviousMesssage('Generating SPC: ')
        maximum      = len(notClustered)
        for i in range(len(notClustered)):
            #print("Generating SPC for node " + str(i) + " out of " + str(len(notClustered)))
            self.progressPercent.calculatePercent(i, maximum)
            whole    = []
            clusters = []
            self.SurroundingStep([notClustered[i]], whole, 0)
            self.Chisel(whole[0], clusters)
            if len(clusters[0]) > 3:
                checked = []
                for j in range(len(clusters[0])):
                    if clusters[0][j] not in checked:
                        curSeed = []
                        self.BiggestIsle([clusters[0][j]], set(clusters[0]),curSeed)
                        checked += curSeed[0]
                        if self.sizeRange[1] >= len(curSeed[0]) >= self.sizeRange[0]:
                            save.append(curSeed[0])
                            break

        self.progressPercent.finishPercent()

        print("finding non-overlappers")
        newCluster = self.nonOverlappers(save)
        if isEmpty(newCluster):
            return False

        print("Found " + str(len(newCluster)) + " non overlapping SPCs. Making a cluster list")
        for i in range(len(newCluster)):
            self.clustered.append(newCluster[i])

        print(str(len(self.clustered)) + " clusters are now existing. Started the network edit.")
        self.networkEditor(newCluster)
        return True