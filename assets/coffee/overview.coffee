(($) ->
  $('.new-form').hide()
  $('.edit-form').hide()

  $('button.new').click ->
    $( this ).hide()
    revertEditRow()
    row = $( this ).closest('tr').siblings('.new-form')
    row.show()

  $('button.save-new').click ->
    # ajax-call mit austausch der tr
    formRow = $( this ).closest('tr')
    newRow = formRow.siblings().last().clone()
    copyEditedData(formRow, newRow, true)
    newRow.insertAfter(formRow)
    $( this ).closest('table').find('button.new').show()
    formRow.hide()

  $('a .glyphicon-pencil').click ->
    $('.new-form').hide()
    revertEditRow()
    destinationRow = $( this ).closest('tr').siblings('.edit-form')
    sourceRow = $( this ).closest('tr')
    copyDataForEdit(sourceRow, destinationRow)
    destinationRow.insertAfter(sourceRow)
    sourceRow.addClass('invisible-for-edit')
    sourceRow.hide()
    destinationRow.show()
    $('button.new').show()

  $('button.save-edit').click ->
    sourceRow = $( this ).closest('tr')
    destinationRow = $( this ).closest('tr').siblings('.invisible-for-edit')
    copyEditedData(sourceRow, destinationRow, false)
    destinationRow.removeClass('invisible-for-edit')
    sourceRow.hide()
    destinationRow.show()

)(jQuery);

copyDataForEdit = (sourceRow, destinationRow) ->
    year = sourceRow.find('.year').html()
    daysYear = sourceRow.find('.days-year').html()
    daysLeft = sourceRow.find('.days-left').html()
    destinationRow.find('.year').html(year)
    destinationRow.find('.days-year input').val(daysYear)
    destinationRow.find('.days-left').html(daysLeft)

copyEditedData = (sourceRow, destinationRow, fullCopy) ->
    daysYear = sourceRow.find('.days-year input').val()
    destinationRow.find('.days-year').html(daysYear)
    if(fullCopy)
        year = sourceRow.find('.year select').val()
        daysLeft = sourceRow.find('.days-left').html()
        destinationRow.find('.year').html(year)
        destinationRow.find('.days-left').html(daysLeft)

revertEditRow = () ->
    $('.edit-form').hide()
    oldRow = $('.invisible-for-edit')
    if (oldRow.html() != undefined)
        oldRow.removeClass('invisible-for-edit')
        oldRow.show()