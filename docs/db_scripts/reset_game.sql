// reset a hand
update public.gambler set next_gambler = null, previous_gambler = null;
delete from public.account_entry where amount < 0;
delete from public.game_observer;
delete from public.bet;
delete from public.gambler_entry;
delete from public.gambler;
delete from public.hand;