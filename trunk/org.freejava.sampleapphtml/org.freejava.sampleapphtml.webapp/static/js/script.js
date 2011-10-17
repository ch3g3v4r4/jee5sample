$(function(){
	$('input[type=submit]').click(function(){
		alert($('[name=name]').val())
	});
});