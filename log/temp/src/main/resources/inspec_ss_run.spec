JobName = INSPEC-IP_SS_2017-10-09T22-02-52
DatabaseName = Inspec-IP
InputFilePattern = Inspec*.zip
NormInputLocation = /usr/test/drop/
HadoopOutputLocation = /usr/test/output2/
NormLocation = /usr/test/norm/
RawBackupLocation = /usr/test/backup/
ErrorNormLocation = /usr/test/drop/error/
ExpectedRecordCount = 1
TallysheetLocation = /usr/test/tallysheet/
UCSubmitLocation = /usr/test/uc/
DocumentStartTag = <article  
DocumentEndTag = </article>
DeleteStartTag =
DeleteEndTag =
RootEndTag = </Inspec>
AccessionXPath = /article/contg/accn
ACQMinAge = 2

InputLocation = USER/PQD/MANISH/input
Category = NonPatent
UnmatchedFileLocation = USER/PQD/MANISH/backup/unmatched
Priority = 1
DropOffLocation = USER/PQD/MANISH/output
BucketName = content-bulk-analysis

#env=LOCAL

TempTallysheetLocation=/usr/test/db/
ExecutionPlanLocation=/usr/test/
TallysheetAPIKey=fK0HHx97gU1L1Edk0iHSo6IyYWFrAVhV80aBGfff
TallysheetGenerationServiceEndpoint=<URL>
TallysheetUpdateServiceEndpoint=https://rod5b1fbh9.execute-api.us-east-1.amazonaws.com/test/lambda

ZipFileSeqRegex=<todo>
#ADD.detector=
#UPD.detector=/BIOSISCitation/Header/UpdateInfo
#DEL.detector=

#RecordType = {\
#"Priority": ["Filename", "XPATH"],\
#"Filename": {\
#           "DEL": {"Pattern": "^prev%s\\\\d+\.(zip|ZIP)$","Val":["d"]}\
#          },\
#"XPATH":    {\
#           "UPD": {"Xpath":"/BIOSISCitation/Header/UpdateInfo", "Val":[]}\
#          },\
#"DEFAULT": "ADD"\
#}\

RecordType = {\
  "priority": [\
  ],\
  "defaultType": "ADD"\
}
#xmlValidation=dtd, BIOSISCitationSet SYSTEM "/usr/test/drop/BIOSISPREVIEWS-IP_SS_2017-10-09T22-02-52/BIOSISPreviews.dtd"
xmlValidation=dtd, BIOSISCitationSet PUBLIC " " "https://s3.amazonaws.com/content-bulk-analysis/USER/PQD/SOUVIK/Normalization/dtd/inspec_xml.dtd"
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
