library(readr)
library(tokenizers)
library(stringr)

#read file
Ndata <- read_file("/mnt/6bff8858-1e5c-432a-9605-457272a2426f/test/spark-2.2.1-bin-hadoop2.7/bin/checktrain.txt")#file.choose())

#filter +ve and -ve
Npdata = str_extract_all(Ndata, "__label__2 .*")
Nndata = str_extract_all(Ndata, "__label__1 .*")
rm(Ndata)

#remove __label__[1,2]
Nplist = str_sub(Npdata[[1]], 12,)
Nnlist = str_sub(Nndata[[1]], 12,)
rm(Npdata)
rm(Nndata)

#tokanize
Npterms <- tokenize_words(paste(unlist(Nplist), collapse=''))
Nnterms <- tokenize_words(paste(unlist(Nnlist), collapse=''))
rm(Nplist)
rm(Nnlist)

#count words
Nptab <- table(Npterms[[1]])
Nntab <- table(Nnterms[[1]])
rm(Npterms)
rm(Nnterms)

#make table
Nptf <- data.frame(term = names(Nptab), pve = as.numeric(Nptab))
Nntf <- data.frame(term = names(Nntab), nve = as.numeric(Nntab))
rm(Nptab)
rm(Nntab)

View(Nptf)
View(Nntf)

#merge +ve and -ve tf table
tf = merge(x = Nptf, y = Nntf, by = "term", all = TRUE)
tf[is.na(tf)] <- 0      #empty values to 0

a = sum(tf$pve)
b = sum(tf$nve)


#calc P(x|+ve) & P(x|-ve)
Pxp = data.frame("term" = tf$term, "PofXforPOS" = tf$pve/a)
Pxn = data.frame("term" = tf$term, "PofXforNEG" = tf$nve/b)

#merge P(x|+ve) & P(x|-ve) with negative
tf1 = merge(x=tf, y=Pxp, by = "term", all = TRUE)
tf2 = merge(x=tf1, y=Pxn, by = "term", all = TRUE)

#calc P(x)
Px = data.frame("term" = tf$term, "PofX" = (tf$pve+tf$nve)/(a+b))
tf3 = merge(x=tf2, y=Px, by = "term", all = TRUE)

#calc P(+ve) & P(-ve)
Pp = a/(a+b)
Pn = b/(a+b)


View(tf3)
View(Pxp)
View(Pxn)
View(Px)

################################----test-----#####################################

#load test data
Ntestdata <- read_file("/mnt/6bff8858-1e5c-432a-9605-457272a2426f/test/spark-2.2.1-bin-hadoop2.7/bin/checktest.txt")#file.choose())

#filter +ve and -ve
testp = str_extract_all(Ntestdata,"__label__2 .*")
testn = str_extract_all(Ntestdata,"__label__1 .*")

#remove __label__[1,2]
testp1 = str_sub(testp[[1]], 12,)
testn1 = str_sub(testn[[1]], 12,)

#tokanize
testp2 <- tokenize_words(testp1)
testn2 <- tokenize_words(testn1)

mylist <- list()
rightpos = 0
wrongpos = 0
countnewele = 0

#calc P for +ve set
for(i in testp2 ) {     # i is each input
  querypp = 0
  querynp = 0
  for(j in i){          # j is each term in a input
    if(length(grep(j, tf3$term))==0){
      print("nahi hai")
      print(j)
      #add to list add compute P later
      countnewele = countnewele + 1
      mylist[[countnewele]] <- j
      
    }else {
      pxp = dataw$PofXforPOS
      pxn = dataw$PofXforNEG
      px = dataw$PofX
      
      positivep = pxp/px
      negativep = pxn/px
      
      querypp = querypp + positivep
      querynp = querynp + negativep
    }
    
    querypp = querypp/Pp       # P of being +ve
    querynp = querynp/Pn       # P of being -ve
    
    if(querynp<querypp){
      rightpos = rightpos + 1
    }else{
      wrongpos = wrongpos + 1
    }
    #compute p for new words
    while(countnewele > 0){
      t = mylist[[countnewele]]
      countnewele = countnewele - 1      
    }
  }
}


rightneg = 0
wrongneg = 0

#calc P for +ve set
for(i in testn2 ) {
  querypp = 0
  querynp = 0
  for(j in i){
    if(length(grep(j, tf3$term))==0){
      print("nahi hai")
      print(j)
      #add to list add compute P later
      mylist <- j
    }else {
      pxp = dataw$PofXforPOS
      pxn = dataw$PofXforNEG
      px = dataw$PofX
      
      positivep = pxp/px
      negativep = pxn/px
      
      querypp = querypp + positivep
      querynp = querynp + negativep
    }
    
    querypp = querypp/Pp
    querynp = querynp/Pn
    if(querynp>querypp){
      rightneg = rightneg + 1
    }else{
      wrongneg = wrongneg + 1
    }
  }
}

#accuracy
accuracy = (rightpos+ rightneg)/(rightpos+ rightneg + wrongpos + wrongneg )
print(accuracy)


