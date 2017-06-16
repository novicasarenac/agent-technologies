angular.module('agentsPlayground.AgentsManagementController', [])
	   .controller('AgentsManagementController', function($scope, $rootScope, $location) {
		   var url = window.location;
		   var host = "ws://" + url.hostname + ":" + url.port + "/AgentsPlayground/agentRequest";
		   try {
			   socket = new WebSocket(host);
			   
			   socket.onopen = function() {
				   console.log('Socket connection opened!');
			   }
			   
			   socket.onmessage = function(message) {
				   console.log(message);
			   }
			   
			   socket.onclose = function() {
				   socket = null;
				   console.log('Socket connection closed!');
			   }
		   }catch(excpetion) {
				 console.log('Error!');
		   }
		   
		   function send(messageToSend) {
			   try {
				   socket.send(message);
				   console.log('Message sent!');
			   } catch(exception) {
				   console.log('Sending failed!');
			   }
		   }
	   });