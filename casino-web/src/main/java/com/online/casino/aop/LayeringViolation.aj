
package com.online.casino.aop;

public aspect LayeringViolation {
	declare error
       : SystemArchitecture.dataLayerCall()
          && !SystemArchitecture.inRepositoryLayer()
          && !SystemArchitecture.inBootstrapLayer()
          && !SystemArchitecture.inTestLayer()
          && !SystemArchitecture.inPhyshunLayer()
       : "Only the repository | bootstrap | service | test layers are allowed to access the data layer";

	declare error
       : SystemArchitecture.staticRepositoryLayerCall()
         && !SystemArchitecture.inRepositoryLayer()
         && !SystemArchitecture.inServiceLayer()
       : "Only the service layer is allowed to access the repository layer";
         
	declare error
       : SystemArchitecture.serviceLayerCall()
         && !SystemArchitecture.inServiceLayer()
         && !SystemArchitecture.inWebLayer()
         && !SystemArchitecture.inBootstrapLayer()
         && !SystemArchitecture.inTestLayer()
       : "Only the UI | bootstrap | test layers are allowed to access the service layer";
         
	declare error
       : SystemArchitecture.webLayerCall()
         && !SystemArchitecture.inWebLayer()
       : "Only the UI layer is allowed to access itself";
}