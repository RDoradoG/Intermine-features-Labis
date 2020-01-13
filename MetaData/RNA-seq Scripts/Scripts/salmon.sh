#!/bin/bash
#$ -S /bin/bash
#$ -q all.q
#$ -cwd
#$ -N salmon
#$ -t {sra number}
#$ -pe threads 2
#$ -hold_jid bbduk

mkdir -p Salmon

INFILE_1=`ls -1 Clean/*_clean_R1.fastq.gz | head -n $SGE_TASK_ID | tail -n 1`
INFILE_2=${INFILE_1/_clean_R1.fastq.gz/_clean_R2.fastq.gz}
BASENAME=`basename $INFILE_1`
BASENAME=${BASENAME/_clean_R1.fastq.gz}
if [ -f $INFILE_2 ]
then
  OPTIONS="-1 $INFILE_1 -2 $INFILE_2"
else
  OPTIONS="-r $INFILE_1"
fi
INX=~/Chlamy/references/Creinhardtii/v5.5/annotation/Creinhardtii_concatened.transcriptPhy_salmon
OUT=Salmon/${BASENAME}_salmon
GENEMAP=~/Chlamy/references/Creinhardtii/v5.5/annotation/Creinhardtii_concatened.transcriptPhy.genemap
CMD="salmon quant --index $INX --libType A $OPTIONS -o $OUT --seqBias --gcBias --threads $NSLOTS --geneMap $GENEMAP"
echo $CMD
$CMD
