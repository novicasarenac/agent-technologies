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
				    			changeRunnigAgentsNames();
				    		})
				    		break;
				    	case 'REMOVED_NODE':
				    		$scope.$apply(function() {
				    			$scope.runningAgents = message.runningAgents;
				    			$scope.agentTypes = message.agentTypes;
				    			changeRunnigAgentsNames();
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
				    			changeRunnigAgentsNames();
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
		   $scope.performatives = [];
		   $scope.runningAgentsNames = [];
		   $scope.selected = [];
		   $scope.selectedType = {};
		   $scope.communicationType = 'REST';
		   function initREST() {
			   AgentsManagementFactory.getAgentTypes().success(function(data) {
				   $scope.agentTypes = data;
			   });
			   
			   AgentsManagementFactory.getRunningAgents().success(function(data) {
				  $scope.runningAgents = data;
				  for(var i = 0; i < $scope.runningAgents.length; i++) {
					  $scope.runningAgentsNames.push({'id': i+1, 'label': $scope.runningAgents[i].name});
				  }
			   });
			   
			   AgentsManagementFactory.getPerformatives().success(function(data) {
				   $scope.performatives = data;
			   });
		   } 
		   initREST();
		   
		   var changeRunnigAgentsNames = function() {
			   $scope.runningAgentsNames = [];
			   for(var i = 0; i < $scope.runningAgents.length; i++) {
				  $scope.runningAgentsNames.push({'id': i+1, 'label': $scope.runningAgents[i].name});
			   }
		   }
		   
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
		   
		   $scope.sendACLMessage = function(message) {
			   var sender = {};
			   var receivers = [];
			   var replyTo = {};
			   var selectedTemp = [];
			   for(var k = 0; k < $scope.selected.length; k++) {
				   for(var s = 0; s < $scope.runningAgentsNames.length; s++) {
					   if($scope.runningAgentsNames[s].id == $scope.selected[k].id)
						   selectedTemp.push($scope.runningAgentsNames[s].label);
				   }
			   }
			   
			   for(var i = 0; i < $scope.runningAgents.length; i++) {
				   if($scope.runningAgents[i].name == message.sender)
					   sender = $scope.runningAgents[i];
				   if($scope.runningAgents[i].name == message.replyTo)
					   replyTo = $scope.runningAgents[i];
				   for(var j = 0; j < selectedTemp.length; j++) {
					   if($scope.runningAgents[i].name == selectedTemp[j])
						   receivers.push($scope.runningAgents[i]);
				   }
			   }
			   
			   var aclMessage = {
					   'performative': message.performative,
					   'sender': sender,
					   'receivers': receivers,
					   'replyTo': replyTo,
					   'content': message.content,
					   'contentObj': null,
					   'userArgs': null,
					   'language': message.language,
					   'encoding': message.encoding,
					   'ontology': message.ontology,
					   'protocol': message.protocol,
					   'conversationId': message.conversationId,
					   'replyWith': message.replyWith,
					   'inReplyTo': null,
					   'replyBy': message.replyBy
			   };
			   if($scope.communicationType == 'REST')
				   sendMessageREST(aclMessage);
			   else
				   sendMessageWS(aclMessage);
		   }
		   
		   var sendMessageREST = function(aclMessage) {
			   AgentsManagementFactory.sendMessage(aclMessage).success(function(data) {
			   });
		   }
		   
		   var sendMessageWS = function(aclMessage) {
			   
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