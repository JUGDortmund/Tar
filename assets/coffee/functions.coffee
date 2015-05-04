# Ajax form refresh:
refreshVacationForm = (data) ->
  clientWidth = document.documentElement.clientWidth;
  if clientWidth < 770 then $('html, body').animate({ scrollTop: ($('.vacation-form').offset().top)}, 'slow')
  
  $myForm = $('#vacation-form-panel')
  
  $myForm.html(data).hide().fadeIn( 800 )
  $('.panel-default').matchHeight()
  $myForm.find('.input-group.date').datepicker({
    "format" : "dd.mm.yyyy"
    "autoclose" : true
  })
  $myForm.find('select').select2()

# document ready 
(($) ->
  
  $('.input-group.date').datepicker({
     "format" : "dd.mm.yyyy"
     "autoclose" : true
  }) 

  $('.panel-default select').select2()

  $('.autosubmit').on 'change', ->
    $(this).closest('form').submit()
  
  $('.panel-default').matchHeight()

  $('[data-toggle="filter"]').click ->
    $('.offcanvas-filter').toggleClass 'active'

  $('.vacation-list a, .task-list a').click ->
    $.ajax
      url: $(this).attr('href')
      dataType: "html"
      error: (jqXHR, textStatus, errorThrown) ->
        console.log(textStatus)
      success: (data) ->
        refreshVacationForm(data)
    return false

)(jQuery);
