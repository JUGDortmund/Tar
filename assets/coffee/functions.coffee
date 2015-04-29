(($) ->

  $('.input-group.date').datepicker({
     "format" : "dd.mm.yyyy"
  })

  $('.panel-default select').select2()

  $('.autosubmit').on 'change', ->
    $(this).closest('form').submit()
  

  $('.panel-default').matchHeight()

  $('[data-toggle="filter"]').click ->
    $('.offcanvas-filter').toggleClass 'active'

)(jQuery);