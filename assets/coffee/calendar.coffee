(($) ->

  $('#calendar').fullCalendar
    header: {
      left: 'prev,next today'
      center: 'title'
      right: 'month,basicWeek'
    }
    defaultView: 'month'
    lang: 'de'
    weekends: false
    eventLimit: true
    weekNumbers: true
    events: {
      url: 'myData.json'
    }
    eventClick: (calEvent, jsEvent, view) -> 
      console.log('Event: ' + calEvent)
      $(this).css('border-color', 'red')
)(jQuery);