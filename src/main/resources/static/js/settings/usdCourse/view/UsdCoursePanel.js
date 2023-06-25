Ext.define('UsdCourse.view.UsdCoursePanel', {
    xtype: 'usdcoursepanel',
    extend: 'Ext.form.RcePanel',
    controller: 'usdCourseController',
    title: 'Курс USD',
    header: {
        titleAlign: 'center'
    },
    region: 'center',
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Расчетные данные',
                    collapsible: true,
                    collapsed: true,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'fieldset',
                            id: 'cryptoCourses',
                            collapsible: false,
                            title: 'Крипто курсы',
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            flex: 0.35
                        },
                        {
                            xtype: 'fieldset',
                            id: 'discountsFieldSet',
                            collapsible: false,
                            title: 'Скидки',
                            flex: 0.65,
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    boxLabel: 'Учитывать скидки',
                                    listeners: {
                                        change: 'turnDiscounts'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: "Персональная скидка",
                                    value: 0,
                                    decimalSeparator: '.',
                                    padding: '0 0 2 0',
                                    disabled: true,
                                    hideTrigger: true,
                                    listeners: {
                                        change: 'updateResultAmounts'
                                    },
                                    msgTarget: 'side',
                                    validator: 'validateDiscount'
                                },
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: "Оптовая скидка",
                                    value: 0,
                                    decimalSeparator: '.',
                                    padding: '0 0 2 0',
                                    disabled: true,
                                    hideTrigger: true,
                                    listeners: {
                                        change: 'updateResultAmounts'
                                    },
                                    msgTarget: 'side',
                                    validator: 'validateDiscount'
                                },
                                {
                                    xtype: 'panel',
                                    frame: true,
                                    hidden: true,
                                    padding: '5 5 5 5',
                                    style: {
                                        borderColor: '#919191',
                                        borderWidth: '1px'
                                    },
                                    html: 'Введите положительное значение для скидки, либо отрицательное для надбавки.'
                                }
                            ]
                        },
                    ]
                }
            ],
            listeners: {
                afterrender: 'cryptoCoursesAfterRender'
            }
        },
        {
            xtype: 'form',
            id: 'coursesForm',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            buttonAlign: 'center',
            buttons: [
                {
                    text: 'Сохранить',
                    iconCls: 'fa-regular fa-floppy-disk',
                    cls: 'saveBtn',
                    handler: 'onSaveClick'
                },
                {
                    text: 'Восстановить значения',
                    iconCls: 'fa-solid fa-xmark',
                    cls: 'blueButton',
                    handler: 'returnValues'
                }
            ]
        }
    ]
});
