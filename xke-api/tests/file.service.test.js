import { sanitizeFile } from '../service/file.service';
import xkeFile from './slots.json';
import pmock from 'pmock';
// import slots from './slots.json';

beforeEach(() => {
    try {
        this.env = pmock.env({
            SLOTS_TO_EXCLUDE: 'XKE,Fondations,Déjeuner,New Comers,Notation Globale'
        });
    }catch (e) {}
});

test('sanitizeFile: should remove first useless lines and keep only 18 cols', () => {
    //todo
    // when

    // console.log('0: ', xkeFile.Body);
    // console.log('1: ', xkeFile.Body.toString());
    // console.log('2: ', JSON.parse(xkeFile.Body.toString()));
    // const sanitizedFile = sanitizeFile(xkeFile);


});
