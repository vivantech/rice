// generates master drop and create sql

PROJECT_DIR = '/java/projects/rice'

if (args.length > 2) { 
	println 'usage: groovy dball.groovy [-pdir PROJECT_DIR]'
	println '       PROJECT_DIR defaults to ' + PROJECT_DIR
	System.exit(1)	
}

count = 0
for (arg in args) {
   	if (arg == '-pdir') PROJECT_DIR = args[count + 1]
	count++
}	

EOL = '\r\n'
// set up variables based on PROJECT_DIR
// Just add comma separated values for ignored DDL
IGNORES = ['FS_UNIVERSAL_USR_T','SH_CMP_TYP_T','SH_CAMPUS_T','SH_EMP_STAT_T','SH_EMP_TYP_T']
MODULES = ['kns', 'kew', 'ksb', 'kim', 'ken', 'kom', 'kcb']
MASTER_DESTROY_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_db_destroy.sql' 
MASTER_CREATE_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_db_bootstrap.sql'

SAMPLEAPP_DESTROY_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app_drops.sql'
SAMPLEAPP_DATA_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sample_app.sql'

MASTERSAMPLEAPP_DESTROY_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sampleapp_destroy.sql' 
MASTERSAMPLEAPP_CREATE_SQL = PROJECT_DIR + '/kns/src/main/config/sql/rice_sampleapp_create.sql'

// prompt and read user input
println warningtext()
input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
    System.exit(2)
}

// HANDLE DESTROY SQL
println "Creating master drop SQL: " + MASTER_DESTROY_SQL

// The file that contains drop statements
destroySql = getEmptyFile(MASTER_DESTROY_SQL)

// concatenate all sequence and table drops
MODULES.each() {
    moduleName ->
    println "Concatenating drop SQL for module " + moduleName 
    createdrops(destroySql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/sequences');
    createdrops(destroySql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/tables');
}

println "Done."


// HANDLE BOOTSTRAP SQL
println "Creating master bootstrap SQL: " + MASTER_CREATE_SQL

// The file that contains bootstrap statements
bootstrapSql = getEmptyFile(MASTER_CREATE_SQL)

println "Concatenating bootstrap tables, sequences, indexes, and constraints."
MODULES.each() {
    moduleName ->
    println "Concatenating create SQL for module " + moduleName
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/sequences')
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/tables')
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/indexes')
    mergeandstrip(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/ddl/constraints')
}

println "Concatenating bootstrap data."

MODULES.each() {
    moduleName ->
    println "Concatenating bootstrap data for module " + moduleName
    merge(bootstrapSql, PROJECT_DIR + '/' + moduleName + '/src/main/config/sql/' + moduleName.toUpperCase() + 'Bootstrap.sql')
}

println "Merging bootstrap and Sample App sql to create master Sample App sql: " + MASTERSAMPLEAPP_CREATE_SQL + ", " + MASTERSAMPLEAPP_DESTROY_SQL
mastersample_create = getEmptyFile(MASTERSAMPLEAPP_CREATE_SQL)
mastersample_create << "-- " + new Date() << EOL
mastersample_create << "-- This file has been auto-generated from dball.groovy" << EOL
mastersample_create << "-- It contains the master rice bootstrap sql from rice_db_bootstrap.sql" << EOL
mastersample_create << "-- As well as sample data contributed by modules and the rice_sample_create.sql" << EOL
mastersample_create << "--" << EOL
mastersample_create << EOL
mastersample_create << EOL
merge(mastersample_create, MASTER_CREATE_SQL)
merge(mastersample_create, SAMPLEAPP_DATA_SQL)
mastersample_destroy = getEmptyFile(MASTERSAMPLEAPP_DESTROY_SQL)
mastersample_destroy << "-- " + new Date() << EOL 
mastersample_destroy << "-- This file has been auto-generated from dball.groovy" << EOL
mastersample_destroy << "-- It contains the master rice destroy sql from rice_db_destroy.sql" << EOL
mastersample_destroy << "-- As well as the sample app drop script rice_sample_app_drops.sql" << EOL
mastersample_destroy << "--" << EOL
mastersample_destroy << EOL
mastersample_destroy << EOL
// the standard rice modules sample data do not introduce new tables (or at least they shouldn't!)
// so all we really need is the sample app drops
merge(mastersample_destroy, SAMPLEAPP_DESTROY_SQL)
merge(mastersample_destroy, MASTER_DESTROY_SQL)

println "Done."

System.exit(0)

// FUNCTIONS

def getEmptyFile(path) {
    File file = new File(path)
    if (file.exists()) {
        file.delete()
    }
    return file
}

def mergePath(path1, path2) {
    mergeFile(new File(path1), new File(path2))
}

def mergeFile(file1, file2) {
    if (!file2.isFile()) {
       println file2.getAbsolutePath() + " does not exist, skipping"
       return
    }
    file1 << "-- Concatenating " + f.getName() + "\r\n"
    file1 << file2.getText()
    file1 << '\r\n'
}

def merge(file1, path) {
    f = new File(path)
    mergeFile(file1, f)
}

def mergeandstrip(db, dir) {
	d = new File(dir)
    if (!d.isDirectory()) {
       println dir + " does not exist...skipping"
       return
    }
    def p = ~/.*\.ddl/
	d.eachFileMatch(p) {
	    f ->
	    name = f.getName()
	    if (! IGNORES.contains(name.substring(0, name.indexOf(".")))) {
		    f.eachLine {
		        ln -> 
		        if (! (ln.trim().startsWith("TABLESPACE") || ln.trim().startsWith("/*") || ln.trim().startsWith("*"))) {
		            ln = ln.replaceAll('USING INDEX TABLESPACE KUL_IDX01', '')
		            ln = ln.replaceAll('USING INDEX TABLESPACE KUL_IDX02', '')
		            ln = ln.replaceAll('USING INDEX TABLESPACE KUL_IDX03', '')
		            ln = ln.replaceAll('USING INDEX', '')
		            db << ln           
		            db << '\n'
		        }
		    }
	    }
	}
}

def createdrops(db, dir) {
	d = new File(dir)
	if (!d.isDirectory()) {
	   println dir + " does not exist...skipping"
	   return
	}
    def p = ~/.*\.ddl/
	d.eachFileMatch(p) {
	    f ->
	    name = f.getName()
	    if (! IGNORES.contains(name.substring(0, name.indexOf(".")))) {
		    f.eachLine {
		        ln ->
				line = ln.trim().toUpperCase()
				def matcher = line =~ /CREATE TABLE ([\p{Alnum}[_]]+)[ \(]*/
		        if (matcher.matches()) {
		            db << "DROP TABLE ${matcher[0][1]} CASCADE CONSTRAINTS"
		            db << '\n'
		            db << "/"
		            db << '\n'
		        }
		        if (line.startsWith("CREATE SEQUENCE")) {				
		            drop = ln.substring("CREATE SEQUENCE".length() + 1)
		            db << "DROP SEQUENCE ${drop[0 .. (drop.indexOf(' '))]}"
		            db << '\n'
		            db << "/"
		            db << '\n'
		        }
		    }
	    }
	}
}

def warningtext() {
"""
==================================================================
                            WARNING 
==================================================================
It will create or replace the following files:
    1) ${MASTER_DESTROY_SQL}
    2) ${MASTER_CREATE_SQL}

If this is not what you want, please supply more information:
    usage: groovy dball.groovy [-pdir PROJECT_DIR]
           PROJECT_DIR defaults to /java/projects/rice

Do you want to continue (yes/no)?"""
}
