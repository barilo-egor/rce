let ExtUtil = {
    idQuery: function (id) {
        return Ext.ComponentQuery.query('[id=' + id + ']')[0]
    },

    request: function (config) {
        let requestObj = {}
        requestObj.url = config.url
        requestObj.method = config.method ? config.method : 'POST'
        if (config.params) requestObj.params = config.params
        if (config.jsonData) requestObj.jsonData = config.jsonData
        requestObj.failure = this.failure
        requestObj.async = config.async !== false
        requestObj.success = function (rawResponse) {
            let response = Ext.JSON.decode(rawResponse.responseText)
            if (!response.success) {
                let addingText = response.body && response.body.exceptionString
                    ? ': ' + response.body.exceptionString
                    : '. Информация отсутствует.'
                Ext.Msg.alert('Ошибка', 'Ошибка сервера' + addingText)
            } else if (response.body && response.body.warningString){
                Ext.Msg.alert('Внимание', response.body.warningString)
            } else {
                config.success(response, rawResponse)
            }
        }
        Ext.Ajax.request(requestObj)
    },

    failure: function (response) {
        Ext.Msg.alert('Ошибка', 'Ошибка при выполнении запроса. Статус: ' + response.status + '.Описание: ' + response.statusText)
    }
}