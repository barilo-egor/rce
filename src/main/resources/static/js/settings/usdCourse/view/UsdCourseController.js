Ext.define('UsdCourse.view.UsdCourseController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.usdCourseController',

    calculate: function (me) {
        let container = me.up('container')
        let resultInput = container.items.items[3]
        resultInput.setLoading('Загрузка')
        let usdCourseField = container.items.items[0]
        let usdCourse = usdCourseField.value
        let cryptoAmount = container.items.items[2].value
        let params = {
            cryptoAmount: cryptoAmount,
            usdCourse: usdCourse,
            fiatCurrency: usdCourseField.fiatCurrency,
            cryptoCurrency: usdCourseField.cryptoCurrency,
            dealType: usdCourseField.dealType
        }
        Ext.Ajax.request({
            url: '/settings/calculate',
            method: 'GET',
            params: params,
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                resultInput.setValue(response.amount)
                resultInput.setLoading(false)
            }
        })
    }
})