angular.module('agentsPlayground.AgentsManagementController', [])
	   .controller('AgentsManagementController', function($scope, $rootScope, $location, AgentsManagementFactory) {
		   var url = window.location;
		   var host = "ws://" + url.hostname + ":" + url.port + "/AgentsPlayground/agentRequest";
		   
		   try {
			   socket = new WebSocket(host);
			   
			   socket.onopen = function() {
				   console.log('Socket connection opened!');
			   }
			   
			   socket.onmessage = function(messageString) {
				   var message = JSON.parse(messageString.data);
				   switch(message.messageType) {
				    	case 'ADD_RUNNING_AGENT':
				    		$scope.$apply(function() {
				    			$scope.runningAgents.push(message.aid);
				    		})
				    		break;
				    	case 'REMOVED_NODE':
				    		$scope.$apply(function() {
				    			$scope.runningAgents = message.runningAgents;
				    			$scope.agentTypes = message.agentTypes;
				    		})
				    		break;
				    	case 'ADDED_NEW_NODE':
				    		$scope.$apply(function() {
				    			$scope.agentTypes = message.agentTypes;
				    		})
				    		break;
				    	case 'REMOVE_STOPPED_AGENT':
				    		var temp = $scope.runningAgents.filter(running => running.name != message.aid.name);
				    		$scope.$apply(function() {
				    			$scope.runningAgents = temp;
				    		})
				    		break;
				   }
			   }
			   
			   socket.onclose = function() {
				   socket = null;
				   console.log('Socket connection closed!');
			   }
		   }catch(excpetion) {
				 console.log('Error!');
		   }
		   
		   $scope.agentTypes = [];
		   $scope.runningAgents = [];
		   $scope.selectedType = {};
		   $scope.communicationType = 'REST';
		   function initREST() {
			   AgentsManagementFactory.getAgentTypes().success(function(data) {
				   $scope.agentTypes = data;
			   });
			   
			   AgentsManagementFactory.getRunningAgents().success(function(data) {
				   $scope.runningAgents = data;
			   });
		   } 
		   initREST();
		   
		   $scope.runAgent = function(name) {
			   if($scope.communicationType == 'REST')
				   runAgentREST(name);
			   else
				   runAgentWS(name);
		   }
		   
		   var runAgentREST = function(name) {
			   AgentsManagementFactory.runAgent(name, $scope.selectedType).success(function(data) {
				   if(data == null)
					   console.log('Error!');
			   });
		   }
		   
		   var runAgentWS = function(name) {
			   var message = {
					   'name' : name,
					   'newAgentType' : $scope.selectedType,
					   'aid' : null,
					   'agentTypes' : null,
					   'aclMessage' : null,
					   'messageType' : 'RUN_AGENT'
			   };
			   
			   try {
				   var messageJSON = angular.toJson(message);
				   socket.send(messageJSON);
			   } catch(exception) {
				   console.log('Error!');
			   }
		   }
		   
		   $scope.stopAgent = function(agent) {
			   if($scope.communicationType == 'REST')
				   stopAgentREST(agent.name);
			   else
				   stopAgentWS(agent.name);
		   }
		   
		   var stopAgentREST = function(name) {
			   AgentsManagementFactory.stopAgent(name).success(function(data) {
			   });
		   }
		   
		   var stopAgentWS = function(name) {
			   var message = {
					   'name' : name,
					   'newAgentType' : null,
					   'aid' : null,
					   'agentTypes' : null,
					   'aclMessage' : null,
					   'messageType' : 'STOP_AGENT'
			   };
			   
			   try {
				   var messageJSON = angular.toJson(message);
				   socket.send(messageJSON);
			   } catch(exception) {
				   console.log('Error!');
			   }
		   }
		   
		   $scope.selectType = function(type) {
			   $scope.selectedType = type;
		   }
		   
		   $scope.changeCommunicationType = function() {
			   if($scope.communicationType == 'REST')
				   $scope.communicationType = 'WS';
			   else 
				   $scope.communicationType = 'REST';
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