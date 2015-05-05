(($) ->

  getCurrentCheckedStatus = (checkboxId) ->
    return $(checkboxId).is(':checked')

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
    eventSources: [
      {
        url: '/calendar'
        data : ->
          return {
            showApproved : $('#showApproved').is(':checked')
            showPending : $('#showPending').is(':checked')
            showRejected : $('#showRejected').is(':checked')
            showCanceled: $('#showCanceled').is(':checked')
          }
      }
    ]
    eventDataTransform: (eventData) ->
      eventData.color = '#f00'
      eventData.displayedEnd = moment( eventData.end )
      eventData.end = moment( eventData.end ).add(1,'days')
      return eventData
    eventClick: (calEvent, jsEvent, view) -> 
      $vacationDetail = $('#vacationDetail')
      
      $vacationDetail.find('.user').text( calEvent.userFirstName + ' ' + calEvent.userLastName )
      $vacationDetail.find('.state').text( calEvent.state );
      $vacationDetail.find('.time').text( calEvent.start.format('DD.MM.YYYY') + ' - ' + calEvent.displayedEnd.format('DD.MM.YYYY') )
      $vacationDetail.find('.substitute').text( calEvent.substituteFirstName + ' ' + calEvent.substituteLastName )
      $vacationDetail.find('.manager').text( calEvent.managerFirstName + ' ' + calEvent.managerLastName )

      $vacationDetail.show()

      $('#sidebar').addClass('active')

  $('#calendarFilter .checkbox input').on 'click', ->
    $('#calendar').fullCalendar( 'refetchEvents')
)(jQuery);