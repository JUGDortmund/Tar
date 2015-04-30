(($) ->

  getCurrentCheckedStatus = (checkboxId) ->
    console.log('test')
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
    eventClick: (calEvent, jsEvent, view) -> 
      $vacationDetail = $('#vacationDetail')
      
      $vacationDetail.find('.user').text( calEvent.userFirstName + ' ' + calEvent.userLastName )
      $vacationDetail.find('.state').text( calEvent.state );
      $vacationDetail.find('.time').text( calEvent.start.format('DD.MM.YYYY') + ' - ' + calEvent.end.format('DD.MM.YYYY') )
      $vacationDetail.find('.substitute').text( calEvent.substituteFirstName + ' ' + calEvent.substituteLastName )

      $vacationDetail.show()

      $('#sidebar').addClass('active')

  $('#calendarFilter .checkbox input').on 'click', ->
    $('#calendar').fullCalendar( 'refetchEvents')
)(jQuery);