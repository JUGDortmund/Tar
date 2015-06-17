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

  $('form input[id="dateFrom"], form input[id="dateTo"]').blur ->
    $.ajax
      url: 'updateVacationForm'
      method: "POST"
      dataType: "json"
      data: { "id" : $('form > input[id="id"][type="hidden"]').val(), "from": $('form input[id="dateFrom"]').val(), "to": $('form input[id="dateTo"]').val(), "_csrf": $('form.vacationForm > input[name="_csrf"][type="hidden"]').val(), "user": $('form.vacationForm input[id="user"], form.vacationForm select > option[selected="selected"]').val()}
      error: (jqXHR, textStatus, errorThrown) ->
        console.log(textStatus)
    return false

)(jQuery);
