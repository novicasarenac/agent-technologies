angular.module('agentsPlayground.AgentsManagementController', [])
	   .controller('AgentsManagementController', function($scope, $rootScope, $location, AgentsManagementFactory) {
		   var url = window.location;
		   var host = "ws://" + url.hostname + ":" + url.port + "/AgentsPlayground/agentRequest";
		   
		   $scope.agentTypes = [];
		   $scope.runningAgents = [];
		   function initREST() {
			   AgentsManagementFactory.getAgentTypes().success(function(data) {
				   $scope.agentTypes = data;
			   });
			   
			   AgentsManagementFactory.getRunningAgents().success(function(data) {
				   $scope.runningAgents = data;
			   });
		   } 
		   initREST();
		   
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