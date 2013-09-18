/**
 * Copyright (c) 2012, Ooyala, Inc.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * •    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * •    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

{
    "tabTip": "Ooyala Videos",
    "id": "cfTab-Ooyala",
    "iconCls": "cq-cft-tab-icon ooyala",
    "xtype": "ooyalacontentfindertab",
    "suggest": true,
    "ranking": 20,
    "allowedPaths": [
        "/content/*",
        "/etc/scaffolding/*"
    ],
    "items": [
        CQ.wcm.ContentFinderTab.getQueryBoxConfig({
            "id": "cfTab-Ooyala-QueryBox",
            "height": 150,
            "items": [
                CQ.wcm.ContentFinderTab.getSuggestFieldConfig({
                    "xtype": "ooyalasuggestfield",
                    "url": "/bin/wcm/ooyala/suggestions.json"
                }),
                Ooyala.ContentFinderRadioGroup()
            ]
        }),
        CQ.wcm.ContentFinderTab.getResultsBoxConfig({
            "itemsDDGroups": [ "ooyala" ],
            "itemsDDNewParagraph": {
                "path": "ooyala/components/content/ooyalaVideo",
                "propertyName": "./videoid"
            },
            "disableContinuousLoading": false,
            "items": {
                "cls": "cq-cft-dataview cq-cft-ooyalaview",
                "tpl":
                    '<tpl for=".">' +
                        '<div style="" class="cq-cft-ooyala-vid">' +
                            '<img class="cq-cft-ooyala-image" src="{preview_image_url}"/>' +
                            '<div class="cq-cft-ooyala-ddproxy-wrapper"><h4>{name}</h4>' +
                            '<div class="cq-cft-ooyala-duration">{duration}</div></div>' +
                            '<div class="cq-cft-ooyala-separator"></div>' +
                        '</div>' +
                    '</tpl>',
                "itemSelector":  ".cq-cft-ooyala-vid"
            },
            "tbar": [
                CQ.wcm.ContentFinderTab.REFRESH_BUTTON
            ]
        },{
            "a" : function(){console.log('this is a'); },
            "url": "/bin/nrl/ooyala?path="+encodeURIComponent(window.location.hash)
        }, {
            "reader": new CQ.Ext.data.JsonReader({
                "totalProperty": "results",
                "root": "hits",
                "fields": [
                    "name", "path", "preview_image_url", "labels", "duration"
                ],
            "id": "path"
        }),
        })
    ]
}