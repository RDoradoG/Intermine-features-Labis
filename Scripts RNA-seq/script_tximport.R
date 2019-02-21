if (!requireNamespace("BiocManager", quietly = TRUE)) {
  if (!requireNamespace("BiocManager", quietly = TRUE)) {
    install.packages("BiocManager")
  }
  BiocManager::install("tximport", version = "3.8")
}

if (!requireNamespace("jsonlite", quietly = TRUE)) {
  install.packages("jsonlite")
}

library(tximport)
library(jsonlite)

rm( list = ls() )

setIDColumn <- function(data, experiment, type) {
  dtfr.salmon <- data.frame(data)
  odlnames <- colnames(dtfr.salmon)
  dtfr.salmon[,'gene_id'] <- row.names(dtfr.salmon)
  dtfr.salmon[,'experiment'] <- experiment
  dtfr.salmon[,'type'] <- type
  dtfr.salmon <- dtfr.salmon[c(setdiff(names(dtfr.salmon), odlnames), odlnames)]
  return(dtfr.salmon)
}

getNames <- function(files, del = '', sep = '/') {
  splitted <- strsplit(files, sep)
  newNames <- c()
  for (i in 1:length(splitted)) {
    lngthspltd <- length(splitted[[i]])
    newNames <- c(newNames, gsub(del, '', splitted[[i]][lngthspltd - 1]))
  }
  return(newNames)
}

createFolder <- function(mainDir, folder) {
  if (file.exists(folder)){
    setwd(file.path(mainDir, folder))
  } else {
    dir.create(file.path(mainDir, folder))
    setwd(file.path(mainDir, folder))
  }
}

verifyExistenceOfExperiment <- function(rootDir, experiment) {
  allExperiments <- list.files(rootDir)
  for(i in 1:length(allExperiments)){
    if(allExperiments[i] == experiment) {
      return(TRUE)
    }
  }
  return(FALSE)
}

runAllExperiments <- function(del, no_execute) {
  rootDir <- getwd()
  allExperiments <- list.files(rootDir)
  for(i in 1:length(allExperiments)) {
    this_run <- TRUE
    for(j in 1:length(no_execute)) {
      if(allExperiments[i] == no_execute[j]) {
        this_run <- FALSE 
      }
    }
    if(this_run) {
      print(paste("Executing ", allExperiments[i], ":", sep = ''))
      if(allExperiments[i] != 'Gonz_lez_Ballester_2010'){
         setwd(rootDir)
         runOneExperiment(allExperiments[i], del)
      }
      #setwd(rootDir)
      #runOneExperiment(allExperiments[i], del)
    }
  }
  
}

runOneExperiment <- function(experiment, del) {
  rootDir <- getwd()
  type <- 'transcriptomic'
  OSSep <- '/'
  salmonDir <- 'Salmon'
  newDir <- 'tximportTranscriptomic'
  filename <- 'expression_values.txt'
  dir <- paste(rootDir, experiment, sep = OSSep)
  if (verifyExistenceOfExperiment(rootDir, experiment)) {  
    setwd(rootDir)
    runTXimport(dir, salmonDir, OSSep, del, experiment, type, newDir, filename, 'gene')
    setwd(rootDir)
    runTXimport(dir, salmonDir, OSSep, del, experiment, type, newDir, filename, 'transcript')
  } else {
    stop(paste('the experiment', experiment, 'does not exists.', sep = ' '))
  }
}

runTXimport <- function(dir, salmonDir, OSSep, del, experiment, type, newDir, filename, type_execution) {
  if(type_execution == 'gene') {
    fileExt <- 'quant.genes.sf'
  }
  if(type_execution == 'transcript') {
    fileExt <- 'quant.sf'
  }
  files <- file.path(dir, salmonDir, list.files(paste(dir, salmonDir, sep = OSSep)), fileExt)
  names(files) <- getNames(files, del = del, sep = OSSep)
  txi.salmon <- tximport(files, type = "salmon", txOut = TRUE)
  counts <- setIDColumn(txi.salmon$counts, experiment, type)
  abundance <- setIDColumn(txi.salmon$abundance, experiment, type)
  createFolder(dir, newDir)
  write.table(counts, file = paste(type_execution, 'counts_', filename, sep = ''), sep = "\t", quote = FALSE, row.names = FALSE)
  write.table(abundance, file = paste(type_execution, 'abundace_', filename, sep = ''), sep = "\t", quote = FALSE, row.names = FALSE)
}

setwd("/home/rdorado/Chlamy/")

args = commandArgs(trailingOnly=TRUE)

type <- args[1] #one, all
del <- "SRR[0-9]*_"

if (type == 'one') {
  experiment <- args[2] #'ExperimentA'
  runOneExperiment(experiment, del)
}

if (type == 'all') {
  no_execute <- c('references', 'Scripts', 'expression_values')
  runAllExperiments(del, no_execute)
}  
