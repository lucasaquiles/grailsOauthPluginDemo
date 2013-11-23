package grailsoauthplugindemo

import grails.converters.JSON
import org.scribe.model.Token

class OauthCallBackController {

    def oauthService

    def index() {
        render view: '/index'
    }

    def facebookSuccess() {
        Token facebookAccessToken = (Token) session[oauthService.findSessionKeyForAccessToken('facebook')]
        if (facebookAccessToken) {
            def facebookResource = oauthService.getFacebookResource(facebookAccessToken, "https://graph.facebook.com/me")
            def facebookResponse = JSON.parse(facebookResource?.getBody())

            Map data = [id: facebookResponse.id, username: facebookResponse.username, name: facebookResponse.name, email: facebookResponse.email,
                    first_name: facebookResponse.first_name, last_name: facebookResponse.last_name, birthday: facebookResponse.birthday,
                    gender: facebookResponse.gender, link: facebookResponse.link, work: facebookResponse.work, hometown: facebookResponse.hometown,
                    education: facebookResponse.education]

            render view: '/index', model: [provider: 'Facebook', data: data]
        } else {
            flash.error = "Token not found."
            render view: '/index'
        }
    }

    def twitterSuccess() {
        Token twitterAccessToken = (Token) session[oauthService.findSessionKeyForAccessToken('twitter')]
        if (twitterAccessToken) {
            def twitterResource = oauthService.getTwitterResource(twitterAccessToken, "https://api.twitter.com/1.1/account/settings.json")
            def twitterResponse = JSON.parse(twitterResource?.getBody())

            def twitterResourceDetailed = oauthService.getTwitterResource(twitterAccessToken, "https://api.twitter.com/1.1/users/show.json?screen_name=${twitterResponse['screen_name']}")
            def twitterResponseDetailed = JSON.parse(twitterResourceDetailed?.getBody())

            Map data = [id: twitterResponseDetailed.id, screen_name: twitterResponseDetailed.screen_name, name: twitterResponseDetailed.name,
                    lang: twitterResponseDetailed.lang, created_at: twitterResponseDetailed.created_at]

            render view: '/index', model: [provider: 'Twitter', data: data]
        } else {
            flash.error = "Token not found."
            render view: '/index'
        }
    }

    def linkedin() {
        Token linkedinAccessToken = (Token) session[oauthService.findSessionKeyForAccessToken('linkedin')]
        if (linkedinAccessToken) {
            def linkedInResponse = oauthService.getLinkedInResource(linkedinAccessToken, "http://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,date-of-birth)?format=json")
            def linkedinParsedResponse = JSON.parse(linkedInResponse?.getBody())

            Map data = [id: linkedinParsedResponse.id, emailAddress: linkedinParsedResponse.emailAddress, firstName: linkedinParsedResponse.firstName,
                    lastName: linkedinParsedResponse.lastName, dateOfBirth: linkedinParsedResponse.dateOfBirth]

            render view: '/index', model: [provider: 'LinkedIn', data: data]
        } else {
            flash.error = "Token not found."
            render view: '/index'
        }
    }

    def failure() {
        flash.error = "Error."
        render view: '/index'
    }

}
