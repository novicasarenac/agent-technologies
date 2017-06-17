angular.module('agentsPlayground.AgentsManagementFactory', [])
	   .factory('AgentsManagementFactory', function($http) {
		   var factory = {};
		   
		   factory.getAgentTypes = function() {
			   return $http.get('/AgentsPlayground/rest/agents/classes');
		   };
		   
		   factory.getRunningAgents = function() {
			   return $http.get('/AgentsPlayground/rest/agents/running');
		   }
		   
		   return factory;
	   });
	   