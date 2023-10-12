Ext.define('Main.view.components.MainFramePanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'mainframepanel',
    padding: '0 0 0 1',
    id: 'mainFramePanel',
    requires: [
        'Main.view.usdCourse.UsdCoursePanel',
        'Main.view.usdCourse.UsdCourseController',
        'Main.view.components.MainFrameTitle',
        'Main.view.components.MainFrameHeader',
        'Main.view.registration.RegistrationPanel',
        'Main.view.registration.RegistrationController',
        'Main.view.api.registration.ApiRegistrationPanel',
        'Main.view.api.registration.ApiRegistrationController',
        'Main.view.api.control.ApiUsersControlPanel',
        'Main.view.api.control.ApiUsersControlController',
        'Main.view.changePassword.ChangePasswordPanel',
        'Main.view.bulkDiscount.BulkDiscountPanel',
        'Main.view.bulkDiscount.BulkDiscountGridPanel',
        'Main.view.bulkDiscount.BulkDiscountController',
        'Main.view.bulkDiscount.BulkDiscountAddForm',
        'Main.view.paymentTypes.PaymentTypesPanel',
    ],
    layout: {
        type: 'fit'
    },
    items: [
        {
            xtype: 'paymenttypespanel',
            scrollable: true
        }
    ]
})