
  $(function() {
    $( "#tabs" ).tabs();
  });


function initDatabase() {
	try {
	    if (!window.openDatabase) {
	        alert('Databases are not supported in this browser.');
	    } else {
	        var shortName = 'DEMODB';
	        var version = '1.0';
	        var displayName = 'DEMO Database';
	        var maxSize = 100000; // bytes
	        DEMODB = openDatabase(shortName, version, displayName, maxSize);
			createTables();
			selectAll();
	    }
	} catch(e) {
 
	    if (e == 2) {
	        // Version number mismatch.
	        console.log("Invalid database version.");
	    } else {
	        console.log("Unknown error "+e+".");
	    }
	    return;
	}
}

function createTables(){
	DEMODB.transaction(
        function (transaction) {
        	transaction.executeSql(
        			'CREATE TABLE IF NOT EXISTS deleteT(id INTEGER NOT NULL PRIMARY KEY, login TEXT NOT NULL);'
        			, [], nullDataHandler, errorHandler);
        }
    );
	DEMODB.transaction(
	        function (transaction) {
	        	transaction.executeSql(
	        			'CREATE TABLE IF NOT EXISTS viewed(id INTEGER NOT NULL PRIMARY KEY, login TEXT NOT NULL);'
	        			, [], nullDataHandler, errorHandler);
	        }
	    );
	DEMODB.transaction(
	        function (transaction) {
	        	transaction.executeSql(
	        			'CREATE TABLE IF NOT EXISTS now(id INTEGER NOT NULL PRIMARY KEY, login TEXT NOT NULL);'
	        			, [], nullDataHandler, errorHandler);
	        }
	    );
}

nullDataHandler= function() {
    console.log("SQL Query Succeeded");
}


function selectAll(){
	DEMODB.transaction(
	    function (transaction) {
	        transaction.executeSql("SELECT * FROM deleteT;", [],
	        		dataSelectHandler, errorHandler);
	    }
	);
	
	DEMODB.transaction(
		    function (transaction) {
		        transaction.executeSql("SELECT * FROM viewed;", [],
		        		dataSelectHandlerViewed, errorHandler);
		    }
		);
	
	DEMODB.transaction(
		    function (transaction) {
		        transaction.executeSql("SELECT * FROM now;", [],
		        		dataSelectHandlerNow, errorHandler);
		    }
		);
}

dataSelectHandler= function( transaction, results ) {
	
	var i=0,
	row;
	
	for (i ; i<results.rows.length; i++) {
	    
		row = results.rows.item(i);
		$('#cckk_'+row['id']).prop('checked', true);
	}
	
}

dataSelectHandlerViewed= function( transaction, results ) {
	
	var i=0,
	row;
	
	for (i ; i<results.rows.length; i++) {
	    
		row = results.rows.item(i);
		$('#ccii_'+row['id']).prop('checked', true);
		$('#img_'+row['id']).toggleClass( "lessOpaque" );
	}
	
}

dataSelectHandlerNow= function( transaction, results ) {
	
	var i=0,
	row;
	
	for (i ; i<results.rows.length; i++) {
	    
		row = results.rows.item(i);
		$('#ccpp_'+row['id']).prop('checked', true);
		$('#img_'+row['id']).toggleClass( "lastSaw" );
	}
	
}

errorHandler= function( transaction, error ) {
    
 	if (error.code===1){
 		// DB Table already exists
 	} else {
    	// Error is a human-readable string.
	    console.log('Oops.  Error was '+error.message+' (Code '+ error.code +')');
 	}
    return false;		    
}

$('.check_c').click(function () {
    //console.log($(this).attr('codeId')+"|"+$(this).attr('loginId'));
	var codeId = $(this).attr('codeId');
	var loginId = $(this).attr('loginId')
	
	
	
	DEMODB.transaction(
		    function (transaction) {
		        transaction.executeSql("SELECT * FROM deleteT  WHERE id = ? and login = ?;", [codeId,loginId],
		        		function(transaction, results ){
		        			if(results.rows.length == 0){
		        				var data = [codeId,loginId];
		        				transaction.executeSql("INSERT INTO deleteT(id, login) VALUES (?, ?)", [data[0], data[1]], nullDataHandler, errorHandler);
		        			   
		        			} else {
		        				transaction.executeSql("DELETE FROM deleteT  WHERE id = ? and login = ?;", [codeId,loginId], nullDataHandler, errorHandler);		        			    
		        			}
		        		}, errorHandler);
		    }
		);

	
});

$('.check_i').click(function () {
    //console.log($(this).attr('codeId')+"|"+$(this).attr('loginId'));
	var codeId = $(this).attr('codeId');
	var loginId = $(this).attr('loginId')
	
	
		$('#img_'+codeId).toggleClass( "lessOpaque" );
	
	DEMODB.transaction(
		    function (transaction) {
		        transaction.executeSql("SELECT * FROM viewed  WHERE id = ? and login = ?;", [codeId,loginId],
		        		function(transaction, results ){
		        			if(results.rows.length == 0){
		        				var data = [codeId,loginId];
		        				transaction.executeSql("INSERT INTO viewed(id, login) VALUES (?, ?)", [data[0], data[1]], nullDataHandler, errorHandler);
		        			   
		        			} else {
		        				transaction.executeSql("DELETE FROM viewed  WHERE id = ? and login = ?;", [codeId,loginId], nullDataHandler, errorHandler);		        			    
		        			}
		        		}, errorHandler);
		    }
		);

	
});

$('.check_p').click(function () {
    //console.log($(this).attr('codeId')+"|"+$(this).attr('loginId'));
	var codeId = $(this).attr('codeId');
	var loginId = $(this).attr('loginId')
	
	
		$('#img_'+codeId).toggleClass( "lastSaw" );
	
	DEMODB.transaction(
		    function (transaction) {
		        transaction.executeSql("SELECT * FROM now  WHERE id = ? and login = ?;", [codeId,loginId],
		        		function(transaction, results ){
		        			if(results.rows.length == 0){
		        				var data = [codeId,loginId];
		        				transaction.executeSql("INSERT INTO now(id, login) VALUES (?, ?)", [data[0], data[1]], nullDataHandler, errorHandler);
		        			   
		        			} else {
		        				transaction.executeSql("DELETE FROM now  WHERE id = ? and login = ?;", [codeId,loginId], nullDataHandler, errorHandler);		        			    
		        			}
		        		}, errorHandler);
		    }
		);

	
});

function callLink(element,login){
	

	
//	var url = 'http://search.4shared.com/web/logout?returnTo=' + encodeURIComponent(element.href)
//	var xhr = createCORSRequest('GET', url);
//	if (!xhr) {
//	  throw new Error('CORS not supported');
//	}
//	xhr.withCredentials = true;
//
//		try {
//			xhr.send(null);
//		} catch (err) {
//			console.log(err.message);
//		}

		
		
		url = "https://www.4shared.com/web/login";
		var http = createCORSRequest('POST', url);
		if (!http) {
		  throw new Error('CORS not supported');
		}
		http.withCredentials = true;
		var params = 
			"login="+encodeURIComponent(login)
			+"&password=qwer1234&returnTo="
			+encodeURIComponent(element.href)
			+"&remember=on&_remember=on";

		//Send the proper header information along with the request
		http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		

		http.onreadystatechange = function() {//Call a function when the state changes.
		    if(http.readyState == 4 && http.status == 200) {
		        alert(http.responseText);
		    }
		}
		
		try {
			http.send(params);
		} catch (err) {
			console.log(err.message);
		}
		
}


function createCORSRequest(method, url) {
	  var xhr = new XMLHttpRequest();
	  if ("withCredentials" in xhr) {

	    // Check if the XMLHttpRequest object has a "withCredentials" property.
	    // "withCredentials" only exists on XMLHTTPRequest2 objects.
	    xhr.open(method, url, false);

	  } else if (typeof XDomainRequest != "undefined") {

	    // Otherwise, check if XDomainRequest.
	    // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
	    xhr = new XDomainRequest();
	    xhr.open(method, url, false);

	  } else {

	    // Otherwise, CORS is not supported by the browser.
	    xhr = null;

	  }
	  return xhr;
	}