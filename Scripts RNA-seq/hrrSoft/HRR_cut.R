library(optparse)
library(parallel)

option_list = list(make_option(c("-n", "--files"), type="integer", default=NULL, help="Number of .txt files (equal to number of cpus used in hrr)", metavar="integer"),
		   make_option(c("-a", "--accession"), type="character", default=NULL, help="File containing transcript names", metavar="character"),
		   make_option(c("-s", "--size"), type="integer", default=NULL, help="Number of rows in the processed matrix (not REAL size), see hrr standard output", metavar="integer"),
		   make_option(c("-S", "--realsize"), type="integer", default=NULL, help="Number of rows in the matrix (REAL size), see hrr standard output", metavar="integer"),
		   make_option(c("-c", "--subsize"), type="integer", default=NULL, help="Number of row in the submatrix, see hrr standard output", metavar="integer"),
		   make_option(c("-r", "--corr"), type="character", default=NULL, help="PCC or HRR", metavar="character"),
		   make_option(c("-m", "--mcores"), type="integer", default=NULL, help="Number of CPUs to use; relies on 'Parallel' R package", metavar="integer"),
       make_option(c("-z", "--maxnodes"), type="integer", default=30, help="The max HRR rank of the third level", metavar="integer"))
		   
 
opt_parser<-OptionParser(option_list=option_list)
opt<-parse_args(opt_parser)

if (is.null(opt$files)){
  print_help(opt_parser)
  stop("At least one argument must be supplied (input file).", call.=FALSE)
}

cpus.ok<-opt$files-1
acc.files<-paste(0:cpus.ok, ".txt", sep="")
ids<-scan(opt$accession, what="character")
to.add<-opt$size-opt$realsize
if (length(to.add!=0)) {
	annot<-c(ids, rep(ids[length(ids)], to.add))} else {
	annot<-ids}

paquets.names<-seq(1, length(annot), opt$subsize)
paquets.names<-c(paquets.names, length(annot)+1)
#data.tmp<-cbind.data.frame("seed"="seed", "corrA"="corrA", "corrB"="corrB", "corrC"="corrC")
#write.table(data.tmp, file = "Result.txt", append = TRUE, quote = FALSE, row.names = FALSE, col.names = FALSE, sep = "\t")
#
mclapply(1:length(acc.files), function(y){
  a<-scan(acc.files[y], what="character")
  acc.names<-annot[paquets.names[y]:(paquets.names[y+1]-1)]
  paquets<-seq(1,length(a), opt$size)
  paquets<-c(paquets, length(a)+1)
  lapply(1:(length(paquets)-1), function(x){
    file.name.tmp<-paste(as.vector(acc.names[x]), sep="")
    table.tmp<-cbind.data.frame("lt"=annot, "corr"=as.numeric(a[paquets[x]:(paquets[(x+1)]-1)]))
    #write.table(table.tmp, file.name.tmp)
    #table.tmpF<-cbind.data.frame("seed"=file.name.tmp, "corrA"=paste(as.character(table.tmp[which(table.tmp["corr"] < opt$maxnodes & table.tmp["lt"] != file.name.tmp), 'lt']), collapse = "\t"), "corrB"=paste(as.character(table.tmp[which(table.tmp["corr"] > opt$corrx & table.tmp["corr"] < opt$corry), 'lt']), collapse = ","), "corrC"=paste(as.character(table.tmp[which(table.tmp["corr"] > opt$corry & table.tmp["corr"] < opt$maxnodes), 'lt']), collapse = ","))
    choosenOnes <- table.tmp[which(table.tmp["corr"] < opt$maxnodes & table.tmp["lt"] != file.name.tmp), ]
    table.tmpF<-cbind.data.frame("seed"=file.name.tmp, "corr"=paste(as.character(paste(choosenOnes[,'lt'], choosenOnes[,'corr'], sep = '+')), collapse = "\t"))
    write.table(table.tmpF, file = "Result.hrr", append = TRUE, quote = FALSE, row.names = FALSE, col.names = FALSE, sep = "\t")
  })
}, mc.cores=opt$mcores)
