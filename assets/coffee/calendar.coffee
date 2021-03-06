(($) ->
    if ( $('#calendar').length > 0 )

        getCurrentCheckedStatus = (checkboxId) ->
            return $(checkboxId).is(':checked')

        selectedLanguage = $('#calendar').data('lang')
        selectedFormat = $('#calendar').data('dateformat').replace(/d/g, 'D').replace(/y/g, 'Y')

        $('#calendar').fullCalendar
            header: {
                left: 'prev,next today'
                center: 'title'
                right: 'month,agendaWeek'
            }
            defaultView: 'month'
            lang: selectedLanguage
            weekends: true
            eventLimit: true
            weekNumbers: true
            firstDay: 1
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
                switch eventData.state
                    when 'approved' then eventData.color = '#008d4c'
                    when 'pending-approvement' then eventData.color = '#f39c12'
                    when 'pending-substitute' then eventData.color = '#f39c12'
                    when 'rejected' then eventData.color = '#dd4b39'
                    when 'canceled' then eventData.color = '#ca195a'
                    else eventData.color = '#00f'
                eventData.displayedEnd = moment( eventData.end )
                if eventData.allDay
                    eventData.end = moment( eventData.end ).add(1,'days')
                return eventData
            eventClick: (calEvent, jsEvent, view) ->
                if calEvent.type is 'VACATION'
                    #Show Details of event Object in div with ID vacationDetail when event is clicked
                    $vacationDetail = $('#vacationDetail')
                    $vacationDetail.find('.user').text( calEvent.userFirstName + ' ' + calEvent.userLastName )
                    $vacationDetail.find('.state').text( calEvent.state );
                    #The type has to be set, as soon as vacations do have different types, like special leave (e.g.)
                    #$vacationDetail.find('.vacation-type').text( calEvent.vacationType );
                    if calEvent.allDay
                      $vacationDetail.find('.time').text( calEvent.start.format('DD.MM.YYYY') + ' - ' + calEvent.displayedEnd.format('DD.MM.YYYY') )
                    else
                      $vacationDetail.find('.time').text( calEvent.start.format('DD.MM.YYYY HH:mm')  + ' - ' + calEvent.displayedEnd.format('DD.MM.YYYY HH:mm') )

                    #Test if substitute is set. If set, show name, else show -
                    if ( ( calEvent.substituteFirstName? ) && ( calEvent.substituteLastName? ) ) then ( substituteText = calEvent.substituteFirstName + ' ' + calEvent.substituteLastName ) else ( substituteText = '-' )
                    $vacationDetail.find('.substitute').text( substituteText )

                    #Test if manager is set. If set, show name, else show -
                    if ( ( calEvent.managerFirstName? ) && ( calEvent.managerLastName? ) ) then ( managerText = calEvent.managerFirstName + ' ' + calEvent.managerLastName ) else ( managerText = '-' )
                    $vacationDetail.find('.manager').text( managerText )

                    $vacationDetail.show()
                    $('#sidebar').addClass('active')
                    $('.panel-default').matchHeight()
                else
                    $vacationDetail = $('#vacationDetail')
                    $vacationDetail.hide()

        $('#calendarFilter .checkbox input').on 'click', ->
            $('#calendar').fullCalendar( 'refetchEvents')

)(jQuery);