import { sanitizeFile } from '../service/file.service';
import xkeFile from './slots';
import pmock from 'pmock';
// import slots from './slots.txt';

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
    const sanitizedFile = sanitizeFile(xkeFile);

});
