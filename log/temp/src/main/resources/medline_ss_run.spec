JobName = MEDLINE-IP_SS_2017-10-09T22-02-52
DatabaseName = MEDLINE-IP
InputFilePattern = medline.*.zip
NormInputLocation = /usr/test/drop/
HadoopOutputLocation = /usr/test/output2/
NormLocation = /usr/test/norm/
RawBackupLocation = /usr/test/backup/
ErrorNormLocation = /usr/test/drop/error/
ExpectedRecordCount = 1
TallysheetLocation = /usr/test/tallysheet/
UCSubmitLocation = /usr/test/uc/
DocumentStartTag = <PubmedArticle>
DocumentEndTag = </PubmedArticle>
DeleteStartTag = <DeleteCitation>
DeleteEndTag = </DeleteCitation>
RootEndTag = </PubmedArticleSet>
AccessionXPath = <PMID Version=\"1\">(.*?)</PMID>
ACQMinAge = 2

InputLocation = USER/PQD/MANISH/input
Category = NonPatent
UnmatchedFileLocation = USER/PQD/MANISH/backup/unmatched
Priority = 1
DropOffLocation = USER/PQD/MANISH/output
BucketName = content-bulk-analysis

env=LOCAL

TempTallysheetLocation=/usr/test/db/
ExecutionPlanLocation=/usr/test/
TallysheetAPIKey=fK0HHx97gU1L1Edk0iHSo6IyYWFrAVhV80aBGfff
TallysheetGenerationServiceEndpoint=<URL>
TallysheetUpdateServiceEndpoint=https://rod5b1fbh9.execute-api.us-east-1.amazonaws.com/test/lambda

ZipFileSeqRegex=medline(.*?).xml.zip
#RecordType=
xmlValidation=
======================================================

##JobName = JOB_Medline_2017-09-14T20:44:30.403Z
##NormInputLocation = s3://content-bulk-analysis/USER/PQD/MANISH/output
###InputLocation = USER/PQD/MANISH/input
##RootEndTag = </PubmedArticleSet>
###Category = NonPatent
###UnmatchedFileLocation = USER/PQD/MANISH/backup/unmatched
###Priority = 1
##DeleteStartTag = <DeleteCitation>
##DatabaseName = Medline
##UCSubmitLocation = s3://content-bulk-analysis/USER/PQD/SOUVIK/ss/uc/
##HadoopOutputLocation = s3://content-bulk-analysis/USER/PQD/SOUVIK/output2/
##RawBackupLocation = s3://content-bulk-analysis/USER/PQD/MANISH/backup/raw
###DropOffLocation = USER/PQD/MANISH/output
##InputFilePattern = Medline.*
###BucketName = content-bulk-analysis
##DeleteEndTag = </DeleteCitation>
##ErrorNormLocation = s3://content-bulk-analysis/USER/PQD/SOUVIK/ss/drop/error
##ExpectedRecordCount = 1
##TallysheetLocation = s3://content-bulk-analysis/USER/PQD/SOUVIK/ss/tallysheet/
##NormLocation = s3://content-bulk-analysis/USER/PQD/SOUVIK/ss/norm/
##ACQMinAge = 2
##DocumentEndTag = </PubmedArticle>
##DocumentStartTag = <PubmedArticle>
##AccessionXPath = <PMID Version="1">(.*?)</PMID>
