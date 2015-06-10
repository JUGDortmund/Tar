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


initializeDatePicker = () ->
  $('.input-group.date').datepicker({
      format : "dd.mm.yyyy"
      weekStart: 1
      daysOfWeekDisabled: '0,6'
      autoclose: true
      todayHighlight: true
  })

  $('.input-group.date.dateFrom').datepicker().on 'changeDate', ->
    if(isNaN($('.input-group.date.dateTo').datepicker('getDate').valueOf()))
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

)(jQuery);
