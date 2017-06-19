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
		   
		   return factory;
	   });
	   