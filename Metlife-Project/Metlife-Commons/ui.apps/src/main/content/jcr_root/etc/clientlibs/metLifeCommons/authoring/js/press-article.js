MetlifeCommons.Components.PressArticle = {
    beforesubmit: function (dialog) {
        var datePublishedSource = dialog.find('itemId',  'publishedDateSource')[0],
        datePublishedDest = dialog.find('itemId',  'datePublished')[0],
        locationSource = dialog.find('itemId',  'locationSource')[0],
        locationDest = dialog.find('itemId',  'locationjcr')[0],
        topicsSource = dialog.find('itemId',  'topicsSource')[0],
        topicsDest = dialog.find('itemId',  'topicsjcr')[0],
        articleTitleSource = dialog.find('itemId',  'titleArticle')[0],
        articleTitleDest = dialog.find('itemId',  'articleTitlejcr')[0];
        datePublishedDest.setValue(datePublishedSource.value);
        locationDest.setValue(locationSource.getValue());
        topicsDest.setValue(topicsSource.getValue());
        articleTitleDest.setValue(articleTitleSource.getValue());
    }
};