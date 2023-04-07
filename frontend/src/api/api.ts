import { request } from './axios'

/**
 * @description 
 */
export class UserService {      
    /**
     * @description
     * @param {string} username 
     * @return {HttpResponse} 
     */
    static async login1(params) {   
        return request('/login',params, 'post')
    }
    static async login2(params) {   
        return request('/login',params, 'post')
    }
    static async login3(params) {   
        return request('/login',params, 'post')
    }
}

export class landRelevant {     
    /**
     * @description 
     * @return {HttpResponse} result
     */
    static async landList(params) {
        return request('/land_list_info',params, 'get')
    }
}
