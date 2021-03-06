package org.codehaus.groovy.grails.plugins.web.api

import grails.plugins.GrailsPluginManager
import groovy.transform.CompileStatic

/**
 * @author Graeme Rocher
 * @deprecated Use {@link org.grails.plugins.web.controllers.api.ControllersApi} instead
 */
@Deprecated
@CompileStatic
class ControllersApi extends org.grails.plugins.web.controllers.api.ControllersApi {

    ControllersApi() {
    }

    ControllersApi(GrailsPluginManager pluginManager) {
        super(pluginManager)
    }
}
