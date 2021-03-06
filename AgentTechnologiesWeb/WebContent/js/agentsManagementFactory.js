angular.module('agentsPlayground.AgentsManagementFactory', [])
	   .factory('AgentsManagementFactory', function($http) {
		   var factory = {};
		   
		   factory.getAgentTypes = function() {
			   return $http.get('/AgentsPlayground/rest/agents/classes');
		   };
		   
		   factory.getRunningAgents = function() {
			   return $http.get('/AgentsPlayground/rest/agents/running');
		   };
		   
		   factory.runAgent = function(name, type) {
			   return $http.put('/AgentsPlayground/rest/agents/running/' + name, type);
		   };
		   
		   factory.stopAgent = function(name) {
			   return $http.delete('/AgentsPlayground/rest/agents/running/' + name);
		   };
		   
		   factory.getPerformatives = function() {
			   return $http.get('/AgentsPlayground/rest/messages');
		   };
		   
		   factory.sendMessage = function(aclMessage) {
			   return $http.post('/AgentsPlayground/rest/messages', aclMessage);
		   };
		   
		   return factory;
	   });
	   