#Scroll to vaction-form in small resolutions
scrollToVacationForm = ->
  clientWidth = document.documentElement.clientWidth;
  if clientWidth < 761 then $('html, body').animate({ scrollTop: ($('#vacation-form-panel').offset().top)}, 'slow')

#Activate Flip-Toggle
activateToggle = () ->
  $('.flip').click ->
       $('.card').toggleClass 'flipped'
       setTimeout (->
         $('.card .front').toggleClass 'invisible'
         $('.card .back').toggleClass 'invisible'
         return
       ), 180
       return

# Ajax form refresh:
refreshVacationForm = (data) ->
  $myForm = $('#vacation-form-panel')
  $myForm.html(data).hide().fadeIn( 800 )
  $('.panel-default').matchHeight()

  initializeDatePicker()

  $myForm.find('select').select2()
  scrollToVacationForm()
  activateToggle()
  initializeAjaxCalculation()
  toggleHalfDay()

refreshVacationDays = (data) ->
  $daysLabel = $('label.vacationDays')
  $remainingLabel = $('label.remainingDays')
  $daysLabel.text(data.vacationDays)
  $remainingLabel.text(data.remainingDays)

toggleHalfDay = () ->
  $('#halfDay').on 'change', ->
    if $(this).is(':checked')
      if(!isNaN($('.input-group.date.dateTo').datepicker('getDate').valueOf()))
        $('.input-group.date.dateTo').datepicker('update', $('.input-group.date.dateFrom').datepicker('getDate'))

      $('#dateToBox').hide()
      $('#halfDayBox').show()
    else
      $('#dateToBox').show()
      $('#halfDayBox').hide()

initializeDatePicker = () ->
   $('.input-group.date').each ->
      $this = $(this)

      $this.datepicker({
        # convert from java simpledate to date format of datepicker api
        format : $this.data('dateformat').replace(/M/g, 'm')
        autoclose : true
        language : $this.data('lang')
        weekStart: 1
        daysOfWeekDisabled: '0,6'
        todayHighlight: true
      })

      if $this.hasClass('dateFrom')
        $this.on 'changeDate', ->
            if(isNaN($('.input-group.date.dateTo').datepicker('getDate').valueOf()) or $('#halfDay').is(':checked') )
                $('.input-group.date.dateTo').datepicker('update', $(this).datepicker('getDate'))


# document ready 
(($) ->
  initializeDatePicker()

  $('[data-toggle="tooltip"]').tooltip()

  $('.panel-default select').select2()

  $('.autosubmit').on 'change', ->
    $(this).closest('form').submit()
  
  $('.panel-default').matchHeight()

  activateToggle()

  $('[data-toggle="filter"]').click ->
    $('.offcanvas-filter').toggleClass 'active'

  $('.edit-vacation a, .approve-vacation a, #newVacation').click ->
    $.ajax
      url: $(this).attr('href')
      dataType: "html"
      error: (jqXHR, textStatus, errorThrown) ->
        console.log(textStatus)
      success: (data) ->
        refreshVacationForm(data)
    return false
    
  initializeAjaxCalculation()
  toggleHalfDay()
)(jQuery);
